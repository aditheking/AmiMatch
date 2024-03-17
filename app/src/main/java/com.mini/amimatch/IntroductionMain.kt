package com.mini.amimatch
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mini.amimatch.RegisterBasicInfo


class IntroductionMain : AppCompatActivity() {

    private lateinit var signupButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction_main)

        signupButton = findViewById(R.id.signup_button)
        signupButton.setOnClickListener {
            openEmailAddressEntryPage()
        }

        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            openLoginPage()
        }
    }

    private fun openLoginPage() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    private fun openEmailAddressEntryPage() {
        val intent = Intent(this, RegisterBasicInfo::class.java)
        startActivity(intent)
    }
}
