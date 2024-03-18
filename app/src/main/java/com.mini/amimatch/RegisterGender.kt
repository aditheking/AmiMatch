package com.mini.amimatch

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterGender : AppCompatActivity() {

    private lateinit var password: String
    private lateinit var user: Users
    private var male = true
    private lateinit var genderContinueButton: Button
    private lateinit var maleSelectionButton: Button
    private lateinit var femaleSelectionButton: Button
    private lateinit var firestore: FirebaseFirestore

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

        firestore = FirebaseFirestore.getInstance()

        maleSelectionButton.setOnClickListener {
            maleButtonSelected()
        }

        femaleSelectionButton.setOnClickListener {
            femaleButtonSelected()
        }

        genderContinueButton.setOnClickListener {
            saveGenderToFirestore()
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

    private fun saveGenderToFirestore() {
        val ownSex = if (male) "male" else "female"
        user.setSex(ownSex)

        if (user.userId.isNullOrEmpty()) {
            Log.e(TAG, "User ID is null or empty")
            return
        }
            // Update Firestore with user's gender
            firestore.collection("users")
                .document(user.userId!!)
                .update("basicInfo.gender", ownSex)
                .addOnSuccessListener {
                    // Proceed to the next step of registration
                    val defaultPhoto = if (male) "defaultMale" else "defaultFemale"
                    user.profileImageUrl = defaultPhoto

                    val intent = Intent(this, RegisterGenderPrefection::class.java)
                    intent.putExtra("password", password)
                    intent.putExtra("classUser", user)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save gender to Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


