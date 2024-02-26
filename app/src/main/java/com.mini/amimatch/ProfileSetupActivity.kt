package com.mini.amimatch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextAge: TextInputEditText
    private lateinit var spinnerGender: Spinner
    private lateinit var buttonUploadPicture: Button
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var editTextBio: TextInputEditText
    private lateinit var editTextInterests: TextInputEditText
    private lateinit var editTextHobbies: TextInputEditText
    private lateinit var spinnerRelationshipStatus: Spinner
    private lateinit var buttonSaveProfile: Button
    private lateinit var toggleButtonGroupPreferredGender: MaterialButtonToggleGroup
    private lateinit var buttonFemale: MaterialButton
    private lateinit var buttonMale: MaterialButton

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        spinnerGender = findViewById(R.id.spinnerGender)
        buttonUploadPicture = findViewById(R.id.buttonUploadPicture)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)
        editTextBio = findViewById(R.id.editTextBio)
        editTextInterests = findViewById(R.id.editTextInterests)
        editTextHobbies = findViewById(R.id.editTextHobbies)
        spinnerRelationshipStatus = findViewById(R.id.spinnerRelationshipStatus)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)
        toggleButtonGroupPreferredGender = findViewById(R.id.toggleButtonGroupPreferredGender)
        buttonFemale = findViewById(R.id.buttonFemale)
        buttonMale = findViewById(R.id.buttonMale)

        buttonUploadPicture.setOnClickListener {
            openGallery()
        }

        buttonSaveProfile.setOnClickListener {
            saveProfile()
        }

        // Populate spinner with options
        val relationshipStatusOptions = arrayOf("Single", "In a Relationship", "Married")
        val relationshipStatusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, relationshipStatusOptions)
        spinnerRelationshipStatus.adapter = relationshipStatusAdapter

        // Toggle button group listener
        toggleButtonGroupPreferredGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonFemale -> {
                        buttonMale.isChecked = false
                    }
                    R.id.buttonMale -> {
                        buttonFemale.isChecked = false
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun saveProfile() {
        val name = editTextName.text?.toString() ?: ""
        val age = editTextAge.text?.toString() ?: ""
        val gender = spinnerGender.selectedItem?.toString() ?: ""
        val bio = editTextBio.text?.toString() ?: ""
        val interests = editTextInterests.text?.toString() ?: ""
        val hobbies = editTextHobbies.text?.toString() ?: ""
        val relationshipStatus = spinnerRelationshipStatus.selectedItem?.toString() ?: ""

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val profileData = hashMapOf(
            "name" to name,
            "age" to age,
            "gender" to gender,
            "bio" to bio,
            "interests" to interests,
            "hobbies" to hobbies,
            "relationshipStatus" to relationshipStatus,
            "profilePictureUri" to selectedImageUri.toString()
        )

        db.collection("profiles")
            .add(profileData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { uri ->
                selectedImageUri = uri
                imageViewProfilePicture.setImageURI(selectedImageUri)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
    }
}
