package com.mini.amimatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mini.amimatch.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            linkProfileWithEmail(it.uid, email)
                            checkProfileExists(user.uid)
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val user = firebaseAuth.currentUser
            checkProfileExists(user?.uid ?: "")
        }
    }

    private fun checkProfileExists(userId: String) {
        firestore.collection("profiles")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val intent = Intent(this, FeedActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, ProfileSetupActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error checking profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun linkProfileWithEmail(userId: String, email: String) {
        // Link user profile with email (e.g., store email in Firestore along with user profile)
        val userRef = firestore.collection("profiles").document(userId)
        userRef.update("email", email)
            .addOnSuccessListener {
                Toast.makeText(this, "Email linked to profile successfully!", Toast.LENGTH_SHORT).show()
                val user = firebaseAuth.currentUser
                user?.let {
                    checkProfileExists(user.uid)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error linking email to profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
