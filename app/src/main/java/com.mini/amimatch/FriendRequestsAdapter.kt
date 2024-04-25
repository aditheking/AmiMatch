package com.mini.amimatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FriendRequestsAdapter(private val friendRequestsList: ArrayList<Users>, private val context: Context, private val db: FirebaseFirestore) :
    RecyclerView.Adapter<FriendRequestsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_request_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: Users = friendRequestsList[position]
        holder.name.text = user.name
    }

    override fun getItemCount(): Int {
        return friendRequestsList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.friend_request_name)
    }
}
