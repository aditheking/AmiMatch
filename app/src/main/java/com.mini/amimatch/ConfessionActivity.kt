package com.mini.amimatch

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class ConfessionActivity : AppCompatActivity() {

    private lateinit var editTextConfession: EditText
    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var confessionAdapter: ConfessionAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentDate: String
    private var confessionCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confession)

        sharedPreferences = getSharedPreferences("ConfessionPrefs", Context.MODE_PRIVATE)
        currentDate = getCurrentDate()

        val lastDate = sharedPreferences.getString("lastDate", "")
        if (lastDate != currentDate) {
            resetConfessionCounter()
        }

        recyclerView = findViewById(R.id.recyclerViewConfessions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        confessionAdapter = ConfessionAdapter(emptyList())
        recyclerView.adapter = confessionAdapter

        editTextConfession = findViewById(R.id.editTextConfession)
        submitButton = findViewById(R.id.submitButton)

        displayDisclaimerDialog()

        submitButton.setOnClickListener {
            submitConfession()


        }

        loadConfessions()

    }

    private fun displayDisclaimerDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Anonymous Confession Disclaimer")
        dialogBuilder.setMessage(
            "Welcome to AMI-MATCH Anonymous Confession\n\n" +
                    "By submitting an anonymous confession, you acknowledge and agree to the following:\n\n" +
                    "1. While your confession will be anonymous, administrators do not have access to the information provided except in cases explicitly declared in the legal terms of service. Refer to the terms of service page for more information.\n" +
                    "2. We Promise Anonymity: We promise to keep your identity anonymous. Fearlessly confess your feelings without worrying about your identity being revealed.\n"+
                    "3. Responsible Usage: You are solely responsible for the content of your confession. Please refrain from posting inappropriate, offensive, or unlawful content.\n" +
                    "4. Community Guidelines: Your confession must adhere to the community guidelines. Do not post content that promotes violence, discrimination, hate speech, or harassment.\n" +
                    "5. Legal Compliance: Comply with all applicable laws and regulations when submitting your confession.\n" +
                    "6. Daily Confession Quota: You are limited to 3 confessions per day. To avoid spamming the platform.\n" +
                    "7. Disclaimer of Liability: Developers do not assume liability for the content of anonymous confessions submitted by users.\n\n" +
                    "By clicking 'I Agree,' you confirm that you have read, understood, and agree to abide by the terms outlined in this disclaimer.\n\n" +
                    "If you do not agree with these terms, please click 'Cancel' to exit the submission process."
        )
        dialogBuilder.setPositiveButton("I Agree") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            finish()
        }
        dialogBuilder.setOnCancelListener {
            finish()
        }
        dialogBuilder.show()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun resetConfessionCounter() {
        confessionCount = 0

        sharedPreferences.edit().putString("lastDate", currentDate).apply()
    }

    private fun submitConfession() {
        if (confessionCount >= 3) {
            Toast.makeText(this, "You have reached your daily confession quota. Please come back tomorrow.", Toast.LENGTH_SHORT).show()
            return
        }
        val confessionText = editTextConfession.text.toString().trim()

        if (confessionText.isEmpty()) {
            Toast.makeText(this, "Please enter a confession", Toast.LENGTH_SHORT).show()
            return
        }

        if (confessionText.length > 150) {
            Toast.makeText(this, "Confession should not exceed 150 words", Toast.LENGTH_SHORT).show()
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

                confessionCount++
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
