package com.mini.amimatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar

class RegisterAge : AppCompatActivity() {
    private var password: String? = null
    private var user: Users? = null
    private val dateFormatter = SimpleDateFormat("MM-dd-yyyy")
    private var ageSelectionPicker: DatePicker? = null
    private var ageContinueButton: Button? = null
    private val ageLimit = 17

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_age)
        user = intent.getSerializableExtra("classUser") as Users?
        password = intent.getStringExtra("password")
        ageSelectionPicker = findViewById(R.id.ageSelectionPicker)
        ageContinueButton = findViewById(R.id.ageContinueButton)
        ageContinueButton?.setOnClickListener { openHobbiesEntryPage() }
    }

    private fun openHobbiesEntryPage() {
        val age = getAge(
            ageSelectionPicker?.year ?: 0,
            ageSelectionPicker?.month ?: 0,
            ageSelectionPicker?.dayOfMonth ?: 0
        )

        if (age > ageLimit) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, ageSelectionPicker?.year ?: 0)
            cal.set(Calendar.MONTH, ageSelectionPicker?.month ?: 0)
            cal.set(Calendar.DAY_OF_MONTH, ageSelectionPicker?.dayOfMonth ?: 0)
            val dateOfBirth = cal.time
            val strDateOfBirth = dateFormatter.format(dateOfBirth)

            user?.let {
                it.setAge(age)
                it.setProfileImageUrl(strDateOfBirth)
            }

            val intent = Intent(this, RegisterHobby::class.java)
            intent.putExtra("password", password)
            intent.putExtra("classUser", user)
            startActivity(intent)
        } else {
            Toast.makeText(
                applicationContext,
                "Age of the user should be greater than $ageLimit !!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dateOfBirth = Calendar.getInstance().apply {
            set(year, month, day)
        }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }
}
