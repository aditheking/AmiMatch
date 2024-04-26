package com.mini.amimatch

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class FriendRequestsAdapter(private val friendRequestsList: ArrayList<Users>,
                            private val context: Context,
                            private val db: FirebaseFirestore,
                            private val listener: FriendRequestActionListener) :
    RecyclerView.Adapter<FriendRequestsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_request_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: Users = friendRequestsList[position]
        val senderId = user.userId
        if (senderId != null) {
            db.collection("users").document(senderId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val profilePhotoUrl = document.getString("profilePhotoUrl")
                        val profileImageUrl = document.getString("profileImageUrl")

                        val imageUrl = if (!profilePhotoUrl.isNullOrEmpty()) {
                            profilePhotoUrl
                        } else {
                            when (profileImageUrl) {
                                "defaultFemale" -> R.drawable.default_woman.toString()
                                "defaultMale" -> R.drawable.default_man.toString()
                                else -> profileImageUrl ?: ""
                            }
                        }

                        if (imageUrl.isNotEmpty()) {
                            Picasso.get().load(imageUrl).into(holder.imageView)
                        } else {
                            holder.imageView.setImageResource(R.drawable.monkey)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "Error fetching user data: $exception")
                }
        }

        holder.btnAccept.setOnClickListener {
            listener.onAcceptFriendRequest(user)
        }

        holder.btnReject.setOnClickListener {
            listener.onRejectFriendRequest(user)
        }

        holder.name.text = user.name
    }

    override fun getItemCount(): Int {
        return friendRequestsList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.mui_image)
        var name: TextView = itemView.findViewById(R.id.friend_request_name)
        var btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        var btnReject: Button = itemView.findViewById(R.id.btnReject)
    }
    }

