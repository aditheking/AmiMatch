package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    private val PREF_NAME = "LoginPrefs"

    private lateinit var mContext: Context
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mEmail = findViewById(R.id.input_email)
        mPassword = findViewById(R.id.input_password)
        mContext = this
        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        init()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            navigateToMainActivity()
        }
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

        val linkResetPassword: TextView = findViewById(R.id.link_reset_password)
        linkResetPassword.setOnClickListener {
            val email = mEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword(email)
            }
        }



        val linkSignUp: TextView = findViewById(R.id.link_signup)
        linkSignUp.setOnClickListener {
            val intent = Intent(this@Login, RegisterBasicInfo::class.java)
            startActivity(intent)
        }
    }

    private fun resetPassword(email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send password reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun updateUserProfile(user: FirebaseUser?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mContext, "Login Successful", Toast.LENGTH_SHORT).show()
                    saveLoginState(true) // Save login state
                    navigateToMainActivity()
                } else {
                    Toast.makeText(mContext, "Failed to update user profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@Login, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}
