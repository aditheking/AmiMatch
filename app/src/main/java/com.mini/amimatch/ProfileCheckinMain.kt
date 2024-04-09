package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mini.amimatch.databinding.ActivityProfileCheckinMatchedBinding

class ProfileCheckinMain : AppCompatActivity() {
    private lateinit var binding: ActivityProfileCheckinMatchedBinding
    private lateinit var mContext: Context
    private var profileImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCheckinMatchedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this

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

        val intent: Intent = getIntent()
        val name = intent.getStringExtra("name")
        val bio = intent.getStringExtra("bio")
        val interest = intent.getStringExtra("interest")
        val about = intent.getStringExtra("about")
        val yearSemester = intent.getStringExtra("year_semester")
        val course = intent.getStringExtra("course")
        val school = intent.getStringExtra("school")
        val distance = intent.getIntExtra("distance", 1)
        val append = if (distance == 1) "mile away" else "miles away"
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

        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman).into(profileImage)
            "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(profileImage)
            else -> Glide.with(mContext).load(profileImageUrl).into(profileImage)
        }

        binding.sendSms.setOnClickListener {
            startPrivateChat()
        }
    }

    private fun startPrivateChat() {
        val intent = Intent(mContext, PrivateChatActivity::class.java)
        startActivity(intent)
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