package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mini.amimatch.SquareImageView


class ProfileCheckinMatched : AppCompatActivity() {
    private lateinit var user: Users
    private lateinit var mContext: Context
    private lateinit var sendSMSButton: Button
    private lateinit var sendEmailButton: Button
    private var distance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_checkin_matched)

        val intent = intent
        user = intent.getParcelableExtra<Users>("classUser") ?: throw IllegalArgumentException("User cannot be null")
        distance = intent.getIntExtra("distance", 1)

        Log.d(TAG, "onCreate: user name is ${user.name}")

        mContext = this

        val toolbar = findViewById<TextView>(R.id.toolbartag)
        toolbar.text = "Matched"

        sendSMSButton = findViewById(R.id.send_sms)
    //    sendEmailButton = findViewById(R.id.send_email)

        val profileName = findViewById<TextView>(R.id.profile_name)
        val profileDistance = findViewById<TextView>(R.id.profile_distance)
        val profileNumbers = findViewById<TextView>(R.id.profile_number)
      //  val profileEmail = findViewById<TextView>(R.id.profile_email)
        val imageView = findViewById<ImageView>(R.id.image_matched)
        val profileBio = findViewById<TextView>(R.id.bio_match)
        val profileInterest = findViewById<TextView>(R.id.interests_match)

        val age = user.age

        profileName.text = "${user.name}, $age"
      //  profileEmail.text = user.userId

        val append = if (distance == 1) "mile away" else "miles away"
        profileDistance.text = "$distance $append"

        if (user.bio.isNullOrEmpty()) {
            profileBio.text = user.bio
        }

        if (user.interest.isNullOrEmpty()) {
            profileInterest.text = user.interest
        }

        val profileImageUrl = user.profileImageUrl
        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman).into(imageView)
            "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(imageView)
            else -> Glide.with(mContext).load(profileImageUrl).into(imageView)
        }

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener { onBackPressed() }

        if (!user.phoneNumber.isNullOrEmpty()) {
            profileNumbers.text = user.phoneNumber
        } else {
            sendSMSButton.isEnabled = false
        }

        sendSMSButton.setOnClickListener { sendSMS(user.phoneNumber!!, user.name!!) }

        sendEmailButton.setOnClickListener { sendEmail(user.userId!!, user.name!!) }
    }

    private fun sendSMS(phoneNumber: String, userName: String) {
        val smsAppOpener = Intent(Intent.ACTION_VIEW)
        smsAppOpener.data = Uri.parse("sms:$phoneNumber")
        smsAppOpener.putExtra("sms_body", "Hi $userName, \nLove to have a coffee with you!!!!")
        startActivity(smsAppOpener)
    }

    private fun sendEmail(email: String, userName: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding our Pink Moon Match!!!")
        intent.putExtra(Intent.EXTRA_TEXT, "Hi $userName, \nLove to have a coffee with you!!!!")
        startActivity(Intent.createChooser(intent, ""))
    }

    companion object {
        private const val TAG = "ProfileCheckinMatched"
    }
}
