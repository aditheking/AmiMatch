package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterHobby : AppCompatActivity() {

    private lateinit var mContext: Context
    private lateinit var hobbiesContinueButton: Button
    private lateinit var sportsSelectionButton: Button
    private lateinit var travelSelectionButton: Button
    private lateinit var musicSelectionButton: Button
    private lateinit var fishingSelectionButton: Button
    private lateinit var userInfo: Users
    private lateinit var password: String
    private var append = ""

    companion object {
        private const val TAG = "RegisterHobby"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_hobby)
        mContext = this

        Log.d(TAG, "onCreate: started")

        val intent = intent
        val profileImageUrl = intent.getStringExtra("profileImageUrl") ?: ""
        val parcel = Parcel.obtain()
        parcel.writeString(profileImageUrl)
        parcel.setDataPosition(0) // Reset the position to read from the beginning
        userInfo = Users(parcel)
        parcel.recycle() // Recycle the Parcel after use
        password = intent.getStringExtra("password") ?: ""
        initWidgets()
        init()
    }



    private fun initWidgets() {
        sportsSelectionButton = findViewById(R.id.sportsSelectionButton)
        travelSelectionButton = findViewById(R.id.travelSelectionButton)
        musicSelectionButton = findViewById(R.id.musicSelectionButton)
        fishingSelectionButton = findViewById(R.id.fishingSelectionButton)
        hobbiesContinueButton = findViewById(R.id.hobbiesContinueButton)

        sportsSelectionButton.alpha = 0.5f
        sportsSelectionButton.setBackgroundColor(Color.GRAY)

        travelSelectionButton.alpha = 0.5f
        travelSelectionButton.setBackgroundColor(Color.GRAY)

        musicSelectionButton.alpha = 0.5f
        musicSelectionButton.setBackgroundColor(Color.GRAY)

        fishingSelectionButton.alpha = 0.5f
        fishingSelectionButton.setBackgroundColor(Color.GRAY)

        sportsSelectionButton.setOnClickListener { sportsButtonClicked() }
        travelSelectionButton.setOnClickListener { travelButtonClicked() }
        musicSelectionButton.setOnClickListener { musicButtonClicked() }
        fishingSelectionButton.setOnClickListener { fishingButtonClicked() }
    }

    private fun sportsButtonClicked() {
        if (userInfo.isSports()) {
            sportsSelectionButton.alpha = 0.5f
            sportsSelectionButton.setBackgroundColor(Color.GRAY)
            userInfo.setSports(false)
        } else {
            sportsSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
            sportsSelectionButton.alpha = 1.0f
            userInfo.setSports(true)
        }
    }

    private fun travelButtonClicked() {
        if (userInfo.isTravel()) {
            travelSelectionButton.alpha = 0.5f
            travelSelectionButton.setBackgroundColor(Color.GRAY)
            userInfo.setTravel(false)
        } else {
            travelSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
            travelSelectionButton.alpha = 1.0f
            userInfo.setTravel(true)
        }
    }

    private fun musicButtonClicked() {
        if (userInfo.isMusic()) {
            musicSelectionButton.alpha = 0.5f
            musicSelectionButton.setBackgroundColor(Color.GRAY)
            userInfo.setMusic(false)
        } else {
            musicSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
            musicSelectionButton.alpha = 1.0f
            userInfo.setMusic(true)
        }
    }

    private fun fishingButtonClicked() {
        if (userInfo.isFishing()) {
            fishingSelectionButton.alpha = 0.5f
            fishingSelectionButton.setBackgroundColor(Color.GRAY)
            userInfo.setFishing(false)
        } else {
            fishingSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
            fishingSelectionButton.alpha = 1.0f
            userInfo.setFishing(true)
        }
    }

    private fun init() {
        hobbiesContinueButton.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }
}
