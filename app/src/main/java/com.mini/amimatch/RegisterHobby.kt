package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

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

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val hobbiesCollection = firestore.collection("hobbies")

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

        // Set onClickListeners for hobby selection buttons
        sportsSelectionButton.setOnClickListener { toggleHobbySelection(sportsSelectionButton, "sports") }
        travelSelectionButton.setOnClickListener { toggleHobbySelection(travelSelectionButton, "travel") }
        musicSelectionButton.setOnClickListener { toggleHobbySelection(musicSelectionButton, "music") }
        fishingSelectionButton.setOnClickListener { toggleHobbySelection(fishingSelectionButton, "fishing") }
    }

    private fun toggleHobbySelection(button: Button, hobby: String) {
        val selected = button.alpha == 1.0f
        if (selected) {
            button.alpha = 0.5f
            button.setBackgroundColor(Color.GRAY)
        } else {
            button.alpha = 1.0f
            button.setBackgroundColor(Color.parseColor("#FF4081"))
        }
        updateHobbyStatus(hobby, !selected)
    }

    private fun updateHobbyStatus(hobby: String, selected: Boolean) {
        when (hobby) {
            "sports" -> userInfo.setSports(selected)
            "travel" -> userInfo.setTravel(selected)
            "music" -> userInfo.setMusic(selected)
            "fishing" -> userInfo.setFishing(selected)
        }
    }

    private fun init() {
        hobbiesContinueButton.setOnClickListener {
            // Save user's hobby information to Firestore
            saveHobbyDataToFirestore()

            // Start MainActivity and finish current activity
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun saveHobbyDataToFirestore() {
        if (userInfo.userId.isNullOrEmpty()) {
            Log.e(TAG, "User ID is null or empty. Cannot save hobby data.")
            return
        }

        val hobbyData = hashMapOf(
            "sports" to userInfo.isSports(),
            "travel" to userInfo.isTravel(),
            "music" to userInfo.isMusic(),
            "fishing" to userInfo.isFishing()
        )

        hobbiesCollection.document(userInfo.userId!!)
            .set(hobbyData)
            .addOnSuccessListener {
                Log.d(TAG, "Hobby data successfully saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving hobby data to Firestore", e)
            }
    }

}
