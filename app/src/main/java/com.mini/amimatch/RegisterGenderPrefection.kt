package com.mini.amimatch

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterGenderPrefection : AppCompatActivity() {

    private lateinit var password: String
    private lateinit var user: Users // Assuming Users is the correct class
    private var preferMale = true
    private lateinit var preferenceContinueButton: Button
    private lateinit var maleSelectionButton: Button
    private lateinit var femaleSelectionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gender_prefection)

        // Retrieve the password and user object from the intent extras
        val intent = intent
        password = intent.getStringExtra("password") ?: ""
        user = intent.getParcelableExtra<Users>("classUser") ?: Users(
            userId = "",
            name = "",
            profileImageUrl = "",
            bio = "",
            interest = "",
            age = 0,
            distance = 0,
            phoneNumber = "",
            sports = false,
            fishing = false,
            music = false,
            travel = false
        )

        // Check if the user object is not null
        if (user != null) {
            // Use the user object as needed
        } else {
            // Handle the case when the user object is null
            Log.e(TAG, "No User object found in intent extras")
            // You may want to finish the activity or handle the error in another way
        }

        maleSelectionButton = findViewById(R.id.maleSelectionButton)
        femaleSelectionButton = findViewById(R.id.femaleSelectionButton)
        preferenceContinueButton = findViewById(R.id.preferenceContinueButton)

        femaleSelectionButton.alpha = 0.5f
        femaleSelectionButton.setBackgroundColor(Color.GRAY)

        maleSelectionButton.setOnClickListener {
            maleButtonSelected()
        }

        femaleSelectionButton.setOnClickListener {
            femaleButtonSelected()
        }

        preferenceContinueButton.setOnClickListener {
            openAgeEntryPage()
        }
    }



    private fun maleButtonSelected() {
        preferMale = true
        maleSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
        maleSelectionButton.alpha = 1.0f
        femaleSelectionButton.alpha = 0.5f
        femaleSelectionButton.setBackgroundColor(Color.GRAY)
    }

    private fun femaleButtonSelected() {
        preferMale = false
        femaleSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
        femaleSelectionButton.alpha = 1.0f
        maleSelectionButton.alpha = 0.5f
        maleSelectionButton.setBackgroundColor(Color.GRAY)
    }

    private fun openAgeEntryPage() {
        val preferSex = if (preferMale) "male" else "female"
        user.preferSex = preferSex
        val intent = Intent(this, RegisterAge::class.java)
        intent.putExtra("password", password)
        intent.putExtra("classUser", user)
        startActivity(intent)
    }
}
