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
        val profileName: TextView = findViewById<TextView>(R.id.profile_name)
        val profileImage: SquareImageView = findViewById<SquareImageView>(R.id.image_matched)
        val profileBio: TextView = findViewById<TextView>(R.id.bio_match)
        val profileInterest: TextView = findViewById<TextView>(R.id.interests_match)
        val profileDistance: TextView = findViewById<TextView>(R.id.profile_distance)

        // Get data from intent
        val intent: Intent = getIntent()
        val name = intent.getStringExtra("name")
        val bio = intent.getStringExtra("bio")
        val interest = intent.getStringExtra("interest")
        val distance = intent.getIntExtra("distance", 1)
        val append = if (distance == 1) "mile away" else "miles away"

        // Set data to views
        profileDistance.text = "$distance $append"
        profileName.text = name
        profileBio.text = bio
        profileInterest.text = interest
        profileImageUrl = intent.getStringExtra("photo")

        // Load profile image
        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman).into(profileImage)
            "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(profileImage)
            else -> Glide.with(mContext).load(profileImageUrl).into(profileImage)
        }

        // Set onClick listener for Private Chat button
        binding.sendSms.setOnClickListener {
            startPrivateChat()
        }
    }

    private fun startPrivateChat() {
        // Start PrivateChatActivity and pass necessary data
        val intent = Intent(mContext, PrivateChatActivity::class.java)
        // Pass any necessary data to the PrivateChatActivity using intent extras
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
