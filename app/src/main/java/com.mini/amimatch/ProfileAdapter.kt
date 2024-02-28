package com.mini.amimatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProfileAdapter(private val listener: OnProfileSelectedListener) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    private val profiles = ArrayList<Profile>()

    fun setProfiles(profilesList: List<Profile>) {
        profiles.clear()
        profiles.addAll(profilesList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.profile_card_item, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.bind(profile)
    }

    override fun getItemCount(): Int {
        return profiles.size
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profilePictureImageView: ImageView = itemView.findViewById(R.id.profilePictureImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val ageTextView: TextView = itemView.findViewById(R.id.ageTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val profile = profiles[position]
                    listener.onProfileSelected(profile)
                }
            }
        }

        fun bind(profile: Profile) {
            nameTextView.text = profile.name
            ageTextView.text = profile.age.toString()

            Glide.with(itemView.context).load(profile.profilePictureUri).into(profilePictureImageView)
        }
    }

    interface OnProfileSelectedListener {
        fun onProfileSelected(profile: Profile)
    }
}
