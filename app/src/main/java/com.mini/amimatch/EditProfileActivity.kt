package com.mini.amimatch

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {
    private lateinit var manButton: Button
    private lateinit var womanButton: Button
    private lateinit var backImageButton: ImageButton
    private lateinit var manTextView: TextView
    private lateinit var womenTextView: TextView
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView
    private lateinit var imageView5: ImageView
    private lateinit var imageView6: ImageView
    private lateinit var selectedImageViews: List<ImageView>
    private var picUris: MutableList<Uri> = mutableListOf()
    private var permissionsRequired = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val mContext: Context = this@EditProfileActivity
    private var permissionStatus: SharedPreferences? = null
    private var sentToSettings = false
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    private val STORAGE_PATH = "images/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE)
        requestMultiplePermissions()
        initializeViews()
    }

    private fun initializeViews() {
        imageView1 = findViewById(R.id.image_view_1)
        imageView2 = findViewById(R.id.image_view_2)
        imageView3 = findViewById(R.id.image_view_3)
        imageView4 = findViewById(R.id.image_view_4)
        imageView5 = findViewById(R.id.image_view_5)
        imageView6 = findViewById(R.id.image_view_6)
        manButton = findViewById(R.id.man_button)
        womanButton = findViewById(R.id.woman_button)
        manTextView = findViewById(R.id.man_text)
        womenTextView = findViewById(R.id.woman_text)
        backImageButton = findViewById(R.id.back)

        selectedImageViews = listOf(imageView1, imageView2, imageView3, imageView4, imageView5, imageView6)

        backImageButton.setOnClickListener { onBackPressed() }

        womanButton.setOnClickListener {
            womenTextView.setTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.colorAccent))
            womanButton.setBackgroundResource(R.drawable.ic_check_select)
            manTextView.setTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.colorAccent))
            manButton.setBackgroundResource(R.drawable.ic_check_unselect)
        }
        manButton.setOnClickListener {
            manTextView.setTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.black))
            manButton.setBackgroundResource(R.drawable.ic_check_select)
            womenTextView.setTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.black))
            womanButton.setBackgroundResource(R.drawable.ic_check_unselect)
        }

        selectedImageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                selectedImageViews = listOf(imageView)
                proceedAfterPermission()
            }
        }

        // Save button click listener
        val saveButton: Button = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            saveProfileToFirestore()
        }
    }

    private fun requestMultiplePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this@EditProfileActivity,
                permissionsRequired[0]
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this@EditProfileActivity,
                permissionsRequired[1]
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this@EditProfileActivity,
                permissionsRequired[2]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                this@EditProfileActivity,
                permissionsRequired,
                PERMISSION_CALLBACK_CONSTANT
            )
            // Save permission status
            val editor = permissionStatus!!.edit()
            editor.putBoolean(permissionsRequired[0], true)
            editor.apply()
        }
    }

    private fun proceedAfterPermission() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@EditProfileActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> cameraIntent()
                "Choose from Gallery" -> galleryIntent()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_FILE)
    }

    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> onCaptureImageResult(data)
                SELECT_FILE -> onSelectFromGalleryResult(data)
            }
        }
    }

    private fun onCaptureImageResult(data: Intent?) {
        val thumbnail = data?.extras?.get("data") as Bitmap?
        selectedImageViews.forEach { imageView ->
            imageView.setImageBitmap(thumbnail)
            // Convert Bitmap to Uri
            thumbnail?.let {
                val picUri = getImageUri(mContext, it)
                picUris.add(picUri)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun onSelectFromGalleryResult(data: Intent?) {
        val bm: Bitmap?
        if (data != null) {
            try {
                val selectedImageUri = data.data
                bm = MediaStore.Images.Media.getBitmap(
                    applicationContext.contentResolver,
                    selectedImageUri
                )
                selectedImageViews.forEach { imageView ->
                    imageView.setImageBitmap(bm)
                    // Convert Bitmap to Uri
                    selectedImageUri?.let {
                        picUris.add(it)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveProfileToFirestore() {
        // Retrieve other profile information from EditText fields
        val aboutText = findViewById<EditText>(R.id.about_edit_text).text.toString()
        val yearSemesterText = findViewById<EditText>(R.id.year_semester_edit_text).text.toString()
        val courseText = findViewById<EditText>(R.id.course_edit_text).text.toString()
        val schoolText = findViewById<EditText>(R.id.school_edit_text).text.toString()
        val gender = if (manButton.isSelected) "Male" else "Female"

        // Retrieve current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if user ID is not null
        userId?.let { uid ->
            // Access the Firestore collection "users1"
            val userRef = FirebaseFirestore.getInstance().collection("users1").document(uid)

            // Create a Map to store the user's profile data
            val userProfile = hashMapOf(
                "about" to aboutText,
                "year_semester" to yearSemesterText,
                "course" to courseText,
                "school" to schoolText,
                "gender" to gender
            )

            // Save the data to Firestore
            userRef.set(userProfile)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Profile updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Upload images to Firebase Storage
                    uploadImagesToStorage(uid)

                    // Create an Intent to pass data to ProfileCheckinMain activity
                    val intent = Intent(this, ProfileCheckinMain::class.java).apply {
                        putExtra("userId", userId)
                        putExtra("about", aboutText)
                        putExtra("year_semester", yearSemesterText)
                        putExtra("course", courseText)
                        putExtra("school", schoolText)
                        putExtra("gender", gender)
                        putStringArrayListExtra("imageUris", ArrayList(picUris.map { it.toString() }))
                    }

                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Error updating profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun uploadImagesToStorage(userId: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        picUris.forEachIndexed { index, picUri ->
            val imageRef = storageRef.child("$STORAGE_PATH$userId/image$index.jpg")

            val uploadTask = imageRef.putFile(picUri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
            }.addOnFailureListener {
            }
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(
                    this@EditProfileActivity,
                    permissionsRequired[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Got Permission
                proceedAfterPermission()
            }
        }
    }

    companion object {
        private const val TAG = "EditProfileActivity"
        private const val PERMISSION_CALLBACK_CONSTANT = 100
    }
}
