package com.mini.amimatch

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MatchUserAdapter(private val usersList: List<Users>, private val context: Context, private val firestore: FirebaseFirestore) :
    RecyclerView.Adapter<MatchUserAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.matched_user_item, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: Users = usersList[position]

        val senderId = user.userId
        if (senderId != null) {
            firestore.collection("users").document(senderId).get()
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
                    Log.e(TAG, "Error fetching user data: $exception")
                }
        }

        holder.name.text = user.name
    }


    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.mui_image)
        var name: TextView = itemView.findViewById(R.id.mui_name)
    }
}
