package com.mini.amimatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ActiveUserAdapter(usersList: List<Users>, context: Context) :
    RecyclerView.Adapter<ActiveUserAdapter.MyViewHolder>() {
    var usersList: List<Users>
    var context: Context

    init {
        this.usersList = usersList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.active_user_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: Users = usersList[position]
        holder.name.text = user.name
        if (!user.profileImageUrl.isNullOrEmpty()) {
            Picasso.get().load(user.profileImageUrl).into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.aui_image)
        var name: TextView = itemView.findViewById(R.id.aui_name)


        init {
            imageView = itemView.findViewById<CircleImageView>(R.id.aui_image)
            name = itemView.findViewById<TextView>(R.id.aui_name)
        }
    }
}
