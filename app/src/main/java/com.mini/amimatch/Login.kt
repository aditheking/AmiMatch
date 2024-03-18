package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class Login : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private lateinit var mContext: Context
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mEmail = findViewById(R.id.input_email)
        mPassword = findViewById(R.id.input_password)
        mContext = this
        mAuth = FirebaseAuth.getInstance()

        init()
    }

    private fun isStringNull(string: String): Boolean {
        return string.isEmpty()
    }

    //----------------------------------------Firebase----------------------------------------

    private fun init() {
        val btnLogin: Button = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener {
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()

            if (isStringNull(email) || isStringNull(password)) {
                Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            updateUserProfile(user)
                        } else {
                            Toast.makeText(mContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        val linkSignUp: TextView = findViewById(R.id.link_signup)
        linkSignUp.setOnClickListener {
            val intent = Intent(this@Login, RegisterBasicInfo::class.java)
            startActivity(intent)
        }
    }

    private fun updateUserProfile(user: FirebaseUser?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mContext, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(mContext, "Failed to update user profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
