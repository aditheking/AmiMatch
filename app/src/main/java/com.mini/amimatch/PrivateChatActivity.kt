package com.mini.amimatch
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mini.amimatch.databinding.ActivityPrivateChatBinding

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
}