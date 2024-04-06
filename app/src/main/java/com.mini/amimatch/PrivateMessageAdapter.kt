package com.mini.amimatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrivateMessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<PrivateMessageAdapter.ViewHolder>() {

    private val firestore: FirebaseFirestore = Firebase.firestore

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderNameTextView: TextView = itemView.findViewById(R.id.senderNameTextView)
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.messageTextView.text = message.text

        val senderId = message.senderId
        if (senderId != null && senderId.isNotEmpty()) {
            firestore.collection("users").document(senderId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val senderName = document.getString("name")
                        holder.senderNameTextView.text = senderName
                    } else {
                        holder.senderNameTextView.text = "Unknown"
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        } else {
            holder.senderNameTextView.text = "Unknown"
        }

        val timestamp = SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault()).format(Date(message.timestamp))
        holder.timestampTextView.text = timestamp
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}
