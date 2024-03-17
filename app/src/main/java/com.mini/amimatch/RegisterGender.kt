package com.mini.amimatch

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterGender : AppCompatActivity() {

    private lateinit var password: String
    private lateinit var user: Users
    private var male = true
    private lateinit var genderContinueButton: Button
    private lateinit var maleSelectionButton: Button
    private lateinit var femaleSelectionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gender)

        val intent = intent
        user = intent.getParcelableExtra<Users>("classUser")!!
        password = intent.getStringExtra("password") ?: ""

        maleSelectionButton = findViewById(R.id.maleSelectionButton)
        femaleSelectionButton = findViewById(R.id.femaleSelectionButton)
        genderContinueButton = findViewById(R.id.genderContinueButton)

        femaleSelectionButton.alpha = 0.5f
        femaleSelectionButton.setBackgroundColor(Color.GRAY)

        maleSelectionButton.setOnClickListener {
            maleButtonSelected()
        }

        femaleSelectionButton.setOnClickListener {
            femaleButtonSelected()
        }

        genderContinueButton.setOnClickListener {
            openPreferenceEntryPage()
        }
    }

    private fun maleButtonSelected() {
        male = true
        maleSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
        maleSelectionButton.alpha = 1.0f
        femaleSelectionButton.alpha = 0.5f
        femaleSelectionButton.setBackgroundColor(Color.GRAY)
    }

    private fun femaleButtonSelected() {
        male = false
        femaleSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"))
        femaleSelectionButton.alpha = 1.0f
        maleSelectionButton.alpha = 0.5f
        maleSelectionButton.setBackgroundColor(Color.GRAY)
    }

    private fun openPreferenceEntryPage() {
        val ownSex = if (male) "male" else "female"
        user.setSex(ownSex)
        val defaultPhoto = if (male) "defaultMale" else "defaultFemale"
        user.profileImageUrl = defaultPhoto

        val intent = Intent(this, RegisterGenderPrefection::class.java)
        intent.putExtra("password", password)
        intent.putExtra("classUser", user)
        startActivity(intent)
    }
}
