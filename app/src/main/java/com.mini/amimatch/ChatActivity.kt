package com.mini.amimatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.google.firebase.messaging.FirebaseMessaging
import com.mini.amimatch.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = ArrayList<Message>()
    private val firestore: FirebaseFirestore = Firebase.firestore
    private var lastSeenMessageTimestamp: Long = 0
    private lateinit var channelId: String
    private lateinit var channelName: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelId = getString(R.string.default_notification_channel_id)
        channelName = getString(R.string.default_notification_channel_name)

        FirebaseMessaging.getInstance().subscribeToTopic("allMessages")
        Log.d("ChatActivity", "Subscribed to topic: allMessages")



        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        fetchMessages()

        val sharedPreferences = getSharedPreferences("com.mini.amimatch.PREFERENCES", Context.MODE_PRIVATE)
        lastSeenMessageTimestamp = sharedPreferences.getLong("lastSeenMessageTimestamp", 0)

        createNotificationChannel()

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
                    if (it.timestamp > lastSeenMessageTimestamp) {
                        sendNotification("New Chat: ${it.text}",snapshot.key ?: "")
                        updateLastSeenMessageTimestamp(it.timestamp)
                    }
                    binding.rvMessages.scrollToPosition(messageList.size - 1)

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun updateLastSeenMessageTimestamp(timestamp: Long) {
        lastSeenMessageTimestamp = timestamp
        val sharedPreferences = getSharedPreferences("com.mini.amimatch.PREFERENCES", Context.MODE_PRIVATE)
        sharedPreferences.edit().putLong("lastSeenMessageTimestamp", timestamp).apply()
    }

    private fun sendMessage() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val messageText = binding.etMessage.text.toString().trim()

        if (currentUser != null && messageText.isNotEmpty()) {
            checkUsernameSaved(currentUser.uid) { isUsernameSaved ->
                if (isUsernameSaved) {
                    sendMessage(currentUser.uid)
                } else {
                    showUsernameDialog()
                }
            }
        }
    }

    private fun sendNotification(message: String, messageId: String) {
        val sharedPreferences = getSharedPreferences("com.mini.amimatch.NOTIFICATION_PREFS", Context.MODE_PRIVATE)
        val sentNotificationIds = sharedPreferences.getStringSet("sentNotificationIds", HashSet()) ?: HashSet()

        if (!sentNotificationIds.contains(messageId)) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent = Intent(this, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_main)
                .setContentTitle("AMI-MATCH")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notificationBuilder.build())

            // Update SharedPreferences to store the sentNotificationIds set
            sentNotificationIds.add(messageId)
            sharedPreferences.edit().putStringSet("sentNotificationIds", sentNotificationIds).apply()
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
        val currentTimeStamp = System.currentTimeMillis()
        val message = Message(
            senderId = senderId,
            senderName = "",
            text = messageText,
            timestamp = currentTimeStamp
        )
        val messageId = database.child("messages").push().key
        if (messageId != null) {
            message.id = messageId
            database.child("messages").child(messageId).setValue(message)
            binding.etMessage.text.clear()
        }
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
