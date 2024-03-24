package com.mini.amimatch

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mini.amimatch.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = ArrayList<Message>()
    private val firestore: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        fetchMessages()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messageList)
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = messageAdapter
    }

    private fun fetchMessages() {
        database.child("messages").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messageList.add(it)
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val messageText = binding.etMessage.text.toString().trim()

        if (currentUser != null && messageText.isNotEmpty()) {
            // Check if the username is already saved
            checkUsernameSaved(currentUser.uid) { isUsernameSaved ->
                if (isUsernameSaved) {
                    sendMessage(currentUser.uid)
                } else {
                    showUsernameDialog()
                }
            }
        }
    }

    private fun showUsernameDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_username, null)
        val etUsername = dialogView.findViewById<EditText>(R.id.etUsername)

        dialog.setView(dialogView)
            .setTitle("Enter Your Username")
            .setPositiveButton("OK") { _, _ ->
                val username = etUsername.text.toString().trim()

                if (username.isNotEmpty()) {
                    saveUsernameToFirestore(username)
                } else {
                    Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun checkUsernameSaved(userId: String, callback: (Boolean) -> Unit) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val isUsernameSaved = documentSnapshot.exists() && documentSnapshot.getString("name") != null
                callback(isUsernameSaved)
            }
            .addOnFailureListener { exception ->
                callback(false)
            }
    }

    private fun saveUsernameToFirestore(username: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userDocument = firestore.collection("users").document(currentUser.uid)
            userDocument
                .set(mapOf("name" to username))
                .addOnSuccessListener {
                    Toast.makeText(this, "Username saved successfully", Toast.LENGTH_SHORT).show()
                    sendMessage(currentUser.uid)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to save username: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun sendMessage(senderId: String) {
        val messageText = binding.etMessage.text.toString().trim()
        val message = Message(
            senderId = senderId,
            senderName = "",
            text = messageText
        )
        val messageId = database.child("messages").push().key
        if (messageId != null) {
            message.id = messageId
            database.child("messages").child(messageId).setValue(message)
            binding.etMessage.text.clear()
        } else {
        }
    }
}
