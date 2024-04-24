package com.mini.amimatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        val expandLabel: TextView = itemView.findViewById(R.id.expandLabel)
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
                }
        } else {
            holder.senderNameTextView.text = "Unknown"
        }

        loadProfilePicture(holder.profileImageView, message.senderId)

        val timestamp = SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault()).format(Date(message.timestamp))
        holder.timestampTextView.text = timestamp

        if (message.text.length > 100) {
            holder.messageTextView.maxLines = 4
            holder.expandLabel.visibility = View.VISIBLE
            holder.expandLabel.setOnClickListener {
                holder.messageTextView.maxLines = Int.MAX_VALUE
                holder.expandLabel.visibility = View.GONE
            }
        } else {
            holder.messageTextView.maxLines = Int.MAX_VALUE
            holder.expandLabel.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    private fun loadProfilePicture(imageView: ImageView, senderId: String) {
        firestore.collection("users").document(senderId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val profilePhotoUrl = document.getString("profilePhotoUrl")
                    val profileImageUrl = document.getString("profileImageUrl")

                    val imageUrl = if (profilePhotoUrl != null) {
                        profilePhotoUrl
                    } else {
                        when {
                            profileImageUrl == "defaultFemale" -> R.drawable.default_woman
                            profileImageUrl == "defaultMale" -> R.drawable.default_man
                            else -> profileImageUrl
                        }
                    }

                    Glide.with(imageView.context)
                        .load(imageUrl)
                        .into(imageView)
                }
            }
            .addOnFailureListener { exception ->
            }
    }
}
