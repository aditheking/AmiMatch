package com.mini.amimatch

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mini.amimatch.databinding.ActivityProfileCheckinMatchedBinding

class ProfileCheckinMain : AppCompatActivity() {
    private lateinit var binding: ActivityProfileCheckinMatchedBinding
    private lateinit var mContext: Context
    private var profileImageUrl: String? = null
    private var userLocation: Location? = null
    private lateinit var gps: GPS
    private var userId: String? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCheckinMatchedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this

        gps = GPS(mContext)
        userId = intent.getStringExtra("userId")

        // Set up views
        val profileName: TextView = findViewById(R.id.profile_name)
        val profileImage: SquareImageView = findViewById(R.id.image_matched)
        val profileBio: TextView = findViewById(R.id.bio_match)
        val profileInterest: TextView = findViewById(R.id.interests_match)
        val profileDistance: TextView = findViewById(R.id.profile_distance)
        val aboutTextView: TextView = findViewById(R.id.about)
        val yearSemesterTextView: TextView = findViewById(R.id.year_semester)
        val courseTextView: TextView = findViewById(R.id.course)
        val schoolTextView: TextView = findViewById(R.id.school)
        val lockIcon: ImageView = findViewById(R.id.lockIcon)


        userLocation = gps.location

        val intent: Intent = getIntent()
        val name = intent.getStringExtra("name")
        val bio = intent.getStringExtra("bio")
        val interest = intent.getStringExtra("interest")
        val about = intent.getStringExtra("about")
        val yearSemester = intent.getStringExtra("year_semester")
        val course = intent.getStringExtra("course")
        val school = intent.getStringExtra("school")
        val distance = calculateDistance(
            userLocation?.latitude ?: 0.0,
            userLocation?.longitude ?: 0.0,
            intent.getDoubleExtra("latitude", 0.0),
            intent.getDoubleExtra("longitude", 0.0)
        )
        val append = if (distance == 1) "KM away" else "KM away"
        profileImageUrl = intent.getStringExtra("profile_image_url")

        profileDistance.text = "$distance $append"
        profileName.text = name
        profileBio.text = bio
        profileInterest.text = interest
        aboutTextView.text = about
        yearSemesterTextView.text = yearSemester
        courseTextView.text = course
        schoolTextView.text = school
        profileImageUrl = intent.getStringExtra("photo")

        val intentProfilePhotoUrl = intent.getStringExtra("profilePhotoUrl")
        if (!intentProfilePhotoUrl.isNullOrEmpty()) {
            profileImageUrl = intentProfilePhotoUrl
        }

        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman)
                .into(profileImage)

            "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(profileImage)
            else -> Glide.with(mContext).load(profileImageUrl).into(profileImage)
        }

        binding.sendSms.setOnClickListener {
            startPrivateChat(userId)
        }
        retrievePrivacySettings(userId)

        val sendRequestButton: Button = findViewById(R.id.send_friend_request_button)

        sendRequestButton.setOnClickListener {
            sendFriendRequest(userId)
        }
    }


    private fun retrievePrivacySettings(userId: String?) {
        userId?.let {
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    val isPrivate = document?.getBoolean("isPrivate") ?: false
                    val isPublic = document?.getBoolean("isPublic") ?: true

                    if (isPrivate) {
                        blurFields()
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
    }

    private fun blurFields() {
        val profileBio: TextView = findViewById(R.id.bio_match)
        val profileInterest: TextView = findViewById(R.id.interests_match)
        val aboutTextView: TextView = findViewById(R.id.about)
        val yearSemesterTextView: TextView = findViewById(R.id.year_semester)
        val courseTextView: TextView = findViewById(R.id.course)
        val schoolTextView: TextView = findViewById(R.id.school)
        val emailButton: Button = findViewById(R.id.send_email)
        val privateChatButton: Button = findViewById(R.id.send_sms)
        val bioTitle: TextView = findViewById(R.id.bioTitle)
        val interestsTitle: TextView = findViewById(R.id.interestsTitle)
        val aboutTitle: TextView = findViewById(R.id.about_text_view)
        val yearSemesterTitle: TextView = findViewById(R.id.year_semester_text_view)
        val courseTitle: TextView = findViewById(R.id.course_text_view)
        val schoolTitle: TextView = findViewById(R.id.school_text_view)
        val ChatTitle: TextView = findViewById(R.id.chattitle)
        val emailTitle: TextView = findViewById(R.id.emailtitle)



        profileBio.visibility = View.INVISIBLE
        profileInterest.visibility = View.INVISIBLE
        aboutTextView.visibility = View.INVISIBLE
        yearSemesterTextView.visibility = View.INVISIBLE
        courseTextView.visibility = View.INVISIBLE
        schoolTextView.visibility = View.INVISIBLE
        emailButton.visibility = View.INVISIBLE
        privateChatButton.isEnabled = false
        bioTitle.visibility = View.INVISIBLE
        interestsTitle.visibility = View.INVISIBLE
        aboutTitle.visibility = View.INVISIBLE
        yearSemesterTitle.visibility = View.INVISIBLE
        courseTitle.visibility = View.INVISIBLE
        schoolTitle.visibility = View.INVISIBLE
        ChatTitle.visibility = View.INVISIBLE
        emailTitle.visibility = View.INVISIBLE



        binding.lockIcon.visibility = View.VISIBLE
        binding.privateMessage.visibility = View.VISIBLE
    }



    private fun sendFriendRequest(userId: String?) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && currentUserUid != null) {
            val senderRequestRef = firestore.collection("friend_requests").document(userId).collection("received").document(currentUserUid)
            val receiverRequestRef = firestore.collection("friend_requests").document(currentUserUid).collection("sent").document(userId)

            val senderData = hashMapOf("userId" to userId)
            val receiverData = hashMapOf("userId" to currentUserUid)

            senderRequestRef.set(senderData)
                .addOnSuccessListener {
                    Log.d(TAG, "Friend request sent successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error sending friend request", e)
                }

            receiverRequestRef.set(receiverData)
                .addOnSuccessListener {
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error sending friend request to receiver", e)
                }
        } else {
            Log.e(TAG, "User ID is null")
        }
    }


    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val theta = lon1 - lon2
        var dist =
            Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(
                Math.toRadians(
                    lat1
                )
            ) * Math.cos(
                Math.toRadians(lat2)
            ) * Math.cos(Math.toRadians(theta))
        dist = Math.acos(dist)
        dist = Math.toDegrees(dist)
        dist = dist * 60 * 1.1515
        val dis = Math.floor(dist).toInt()
        return if (dis < 1) {
            1
        } else dis
    }

    private fun startPrivateChat(userId: String?) {
        if (userId != null) {
            val intent = Intent(mContext, PrivateChatActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        } else {
            Log.e(TAG, "User ID is null")
        }
    }

    fun DislikeBtn(v: View?) {
        val btnClick = Intent(mContext, BtnDislikeActivity::class.java)
        btnClick.putExtra("url", profileImageUrl)
        startActivity(btnClick)
    }

    fun LikeBtn(v: View?) {
        val btnClick = Intent(mContext, BtnLikeActivity::class.java)
        btnClick.putExtra("url", profileImageUrl)
        startActivity(btnClick)
    }
}