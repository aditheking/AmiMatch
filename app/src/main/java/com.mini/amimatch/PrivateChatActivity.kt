package com.mini.amimatch
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mini.amimatch.databinding.ActivityPrivateChatBinding
import org.json.JSONObject

class PrivateChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var privateMessageAdapter: PrivateMessageAdapter
    private val privateMessageList = ArrayList<Message>()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId")
        Log.d("PrivateChatActivity", "Current user ID: ${currentUser?.uid}")
        Log.d("PrivateChatActivity", "Other user ID: $userId")

        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        fetchMessages()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        privateMessageAdapter = PrivateMessageAdapter(privateMessageList)
        binding.rvPrivateChat.layoutManager = LinearLayoutManager(this)
        binding.rvPrivateChat.adapter = privateMessageAdapter
        binding.rvPrivateChat.post {
            binding.rvPrivateChat.scrollToPosition(privateMessageAdapter.itemCount - 1)
        }
    }

    private fun fetchMessages() {
        val chatRoomId = generateChatRoomId(currentUser!!.uid, userId)
        Log.d("PrivateChatActivity", "Chat room ID: $chatRoomId")
        database.child("private_messages").child(chatRoomId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let {
                        privateMessageList.add(it)
                        privateMessageAdapter.notifyDataSetChanged()
                        Log.d("PrivateChatActivity", "Received message: $it")
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (currentUser != null && messageText.isNotEmpty()) {
            val currentTimeStamp = System.currentTimeMillis()
            val message = Message(
                senderId = currentUser.uid,
                senderName = "",
                text = messageText,
                timestamp = currentTimeStamp
            )
            val chatRoomId = generateChatRoomId(currentUser.uid, userId)
            Log.d("PrivateChatActivity", "Sending message to chat room: $chatRoomId")
            val messageId = database.child("private_messages").child(chatRoomId).push().key
            if (messageId != null) {
                message.id = messageId
                database.child("private_messages").child(chatRoomId).child(messageId).setValue(message)
                sendNotification("New Message", messageText)
                binding.etMessage.text.clear()
            } else {
            }
        }
    }

    private fun generateChatRoomId(userId1: String?, userId2: String?): String {
        return if (userId1 != null && userId2 != null) {
            if (userId1 < userId2) {
                "$userId1-$userId2"
            } else {
                "$userId2-$userId1"
            }
        } else {
            ""
        }
    }

    private fun sendNotification(title: String, message: String) {
        Log.d(TAG, "Sending notification: title=$title, message=$message")

        val fcmUrl = "https://fcm.googleapis.com/fcm/send"
        val serverKey = "YOUR_FIREBASE_SERVER_KEY_HERE"

        val notification = JSONObject().apply {
            put("title", title)
            put("message", message)
        }

        val body = JSONObject().apply {
            put("to", "/topics/topic_name")
            put("data", notification)
        }

        val request = object : JsonObjectRequest(Request.Method.POST, fcmUrl, body,
            Response.Listener {
                Log.d(TAG, "Notification sent successfully")
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error sending notification: $error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=$serverKey"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

}