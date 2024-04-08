package com.mini.amimatch

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID


class ConfessionActivity : AppCompatActivity() {

    private lateinit var editTextConfession: EditText
    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var confessionAdapter: ConfessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confession)

        recyclerView = findViewById(R.id.recyclerViewConfessions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        confessionAdapter = ConfessionAdapter(emptyList())
        recyclerView.adapter = confessionAdapter

        editTextConfession = findViewById(R.id.editTextConfession)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            submitConfession()


        }

        loadConfessions()

    }

    private fun submitConfession() {
        val confessionText = editTextConfession.text.toString().trim()

        if (confessionText.isEmpty()) {
            Toast.makeText(this, "Please enter a confession", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = System.currentTimeMillis()

        val confessionId = UUID.randomUUID().toString()

        val db = FirebaseFirestore.getInstance()
        val confessionRef = db.collection("confessions").document(confessionId)
        val confessionMap = hashMapOf(
            "userId" to userId,
            "confessionText" to confessionText,
            "timestamp" to timestamp
        )

        confessionRef.set(confessionMap)
            .addOnSuccessListener {
                Log.d(TAG, "Confession submitted successfully")
                Toast.makeText(this, "Confession submitted successfully", Toast.LENGTH_SHORT).show()
                editTextConfession.text.clear()

                loadConfessions()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error submitting confession", e)
                Toast.makeText(this, "Failed to submit confession", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadConfessions() {
        val db = FirebaseFirestore.getInstance()
        db.collection("confessions")
            .get()
            .addOnSuccessListener { documents ->
                val confessions = mutableListOf<Confession>()
                for (document in documents) {
                    val confession = document.toObject(Confession::class.java)
                    confessions.add(confession)
                }
                updateConfessions(confessions)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching confessions", e)
                Toast.makeText(this, "Failed to load confessions", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateConfessions(confessions: List<Confession>) {
        confessionAdapter.updateConfessions(confessions)
    }

    companion object {
        private const val TAG = "ConfessionActivity"
    }
}
