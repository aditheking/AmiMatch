package com.mini.amimatch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.mini.amimatch.databinding.ActivityProfileBinding
import java.util.UUID

class Profile_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val mContext: Context = this@Profile_Activity
    private var imagePerson: ImageView? = null
    private var name: TextView? = null
    private val userId: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var filePath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: create the page")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mPulsator: PulsatorLayout = findViewById(R.id.pulsator)
        mPulsator.start()
        setupTopNavigationView()
        imagePerson = findViewById<ImageView>(R.id.circle_profile_image)
        name = findViewById<TextView>(R.id.profile_name)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUserId = mAuth.currentUser?.uid

        currentUserId?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name")
                        name?.text = userName
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        val edit_btn = findViewById<ImageButton>(R.id.edit_profile)
        edit_btn.setOnClickListener {
            val intent = Intent(
                this@Profile_Activity,
                EditProfileActivity::class.java
            )
            startActivity(intent)
        }
        val settings = findViewById<ImageButton>(R.id.settings)
        settings.setOnClickListener {
            val intent = Intent(
                this@Profile_Activity,
                SettingsActivity::class.java
            )
            startActivity(intent)
        }
    }

    fun openGallery(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data!!
            uploadImage()
        }
    }

    private fun uploadImage() {
        val currentUserId = mAuth.currentUser?.uid
        currentUserId?.let { userId ->
            val ref = FirebaseStorage.getInstance().reference.child("images/$userId/${UUID.randomUUID()}")
            ref.putFile(filePath)
                .addOnSuccessListener { taskSnapshot ->
                    Log.d(TAG, "uploadImage: Image upload successful")
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        Log.d(TAG, "uploadImage: Download URL retrieved successfully")

                        firestore.collection("users").document(userId)
                            .update("profilePhotoUrl", uri.toString())
                            .addOnSuccessListener {
                                Log.d(TAG, "uploadImage: Profile photo URL updated successfully")

                                Glide.with(this@Profile_Activity)
                                    .load(uri)
                                    .into(binding.circleProfileImage)
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error updating profile photo URL: $e")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error uploading image: $e")
                }
        } ?: run {
            Log.e(TAG, "Current user is null")
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: resume to the page")
        loadProfileData()
    }

    private fun loadProfileData() {
        val currentUserId = mAuth.currentUser?.uid
        currentUserId?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name")
                        val profilePhotoUrl = document.getString("profilePhotoUrl")

                        name?.text = userName

                        profilePhotoUrl?.let { url ->
                            Glide.with(this@Profile_Activity)
                                .load(url)
                                .into(binding.circleProfileImage)
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to load profile data", exception)
                }
        } ?: run {
            Log.e(TAG, "Current user is null")
        }
    }


    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx = findViewById<BottomNavigationViewEx>(R.id.topNavViewBar)
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu = tvEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
    }

    companion object {
        private const val TAG = "Profile_Activity"
        private const val ACTIVITY_NUM = 0
        var active = false
        private const val PICK_IMAGE_REQUEST = 1
    }
}
