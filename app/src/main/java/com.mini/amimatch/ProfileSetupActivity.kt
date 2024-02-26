package com.mini.amimatch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var buttonUploadPicture: Button
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var editTextBio: EditText
    private lateinit var buttonSaveProfile: Button

    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        spinnerGender = findViewById(R.id.spinnerGender)
        buttonUploadPicture = findViewById(R.id.buttonUploadPicture)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)
        editTextBio = findViewById(R.id.editTextBio)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)

        buttonUploadPicture.setOnClickListener {
            openGallery()
        }

        buttonSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun saveProfile() {
        val name = editTextName.text.toString()
        val age = editTextAge.text.toString()
        val gender = spinnerGender.selectedItem.toString()
        val bio = editTextBio.text.toString()

        val db = FirebaseFirestore.getInstance()
        val profileData = hashMapOf(
            "name" to name,
            "age" to age,
            "gender" to gender,
            "bio" to bio
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
            selectedImageUri = data.data!!
            imageViewProfilePicture.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
    }
}
