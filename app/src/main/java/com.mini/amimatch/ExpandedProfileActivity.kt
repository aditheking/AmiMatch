package com.mini.amimatch

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class ExpandedProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var interestsTextView: TextView
    private lateinit var hobbiesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expanded_profile)

        // Initialize views
        profileImageView = findViewById(R.id.profileImageView)
        nameTextView = findViewById(R.id.nameTextView)
        ageTextView = findViewById(R.id.ageTextView)
        bioTextView = findViewById(R.id.bioTextView)
        interestsTextView = findViewById(R.id.interestsTextView)
        hobbiesTextView = findViewById(R.id.hobbiesTextView)

        val profile = intent.getParcelableExtra<Profile>("profile")

        if (profile != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child(profile.profilePictureUri)
            Glide.with(this /* context */)
                .load(storageReference)
                .placeholder(R.drawable.default_profile_picture)
                .error(R.drawable.default_profile_picture)
                .into(profileImageView)

            nameTextView.text = profile.name
            ageTextView.text = profile.age.toString()
            bioTextView.text = profile.bio
            interestsTextView.text = profile.interests.joinToString(", ")
            hobbiesTextView.text = profile.hobbies.joinToString(", ")
        } else {

        }
    }
}
