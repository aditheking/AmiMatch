package com.mini.amimatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MatchUserAdapter(usersList: List<Users>, context: Context) :
    RecyclerView.Adapter<MatchUserAdapter.MyViewHolder>() {
    var usersList: List<Users>
    var context: Context

    init {
        this.usersList = usersList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.matched_user_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: Users = usersList[position]
        holder.name.text = user.name
        holder.profession.text = user.bio
        if (!user.profileImageUrl.isNullOrEmpty()) {
            Picasso.get().load(user.profileImageUrl).into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.mui_image)
        var name: TextView = itemView.findViewById(R.id.mui_name)
        var profession: TextView = itemView.findViewById(R.id.mui_profession)
    }
}
