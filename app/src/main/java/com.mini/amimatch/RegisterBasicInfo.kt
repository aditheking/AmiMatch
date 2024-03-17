package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterBasicInfo : AppCompatActivity() {

    private lateinit var mContext: Context
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var mEmail: EditText
    private lateinit var mUsername: EditText
    private lateinit var mPassword: EditText
    private lateinit var loadingPleaseWait: TextView
    private lateinit var btnRegister: Button
    private lateinit var append: String
    private lateinit var emailPattern: String
    private lateinit var gps: GPS


    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerbasic_info)
        mContext = this
        Log.d(TAG, "onCreate: started")

        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"


        gps = GPS(applicationContext)

        initWidgets()
        init()
    }

    private fun init() {
        btnRegister.setOnClickListener {
            email = mEmail.text.toString()
            username = mUsername.text.toString()
            password = mPassword.text.toString()

            if (checkInputs(email, username, password)) {
                val location: Location? = gps.location
                var latitude = 37.349642
                var longitude = -121.938987
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                }
                Log.d("Location==>", "$longitude   $latitude")

                val user = Users(
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

// Create an intent and pass the password and user object as extras
                val intent = Intent(this@RegisterBasicInfo, RegisterGender::class.java)
                intent.putExtra("password", password)
                intent.putExtra("classUser", user as Parcelable)
                startActivity(intent)
            }
        }
    }

    private fun checkInputs(email: String, username: String, password: String): Boolean {
        Log.d(TAG, "checkInputs: checking inputs for null values.")
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(applicationContext, "Invalid email address, enter valid email id and click on Continue", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun initWidgets() {
        Log.d(TAG, "initWidgets: initializing widgets")
        mEmail = findViewById(R.id.input_email)
        mUsername = findViewById(R.id.input_username)
        btnRegister = findViewById(R.id.btn_register)
        mPassword = findViewById(R.id.input_password)
        mContext = this
    }

    fun onLoginClicked(view: View) {
        startActivity(Intent(applicationContext, Login::class.java))
    }
}
