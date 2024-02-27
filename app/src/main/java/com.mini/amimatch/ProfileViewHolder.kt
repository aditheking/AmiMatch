package com.mini.amimatch
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mini.amimatch.Profile
import com.mini.amimatch.R

class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    private val ageTextView: TextView = itemView.findViewById(R.id.ageTextView)
    private val genderTextView: TextView = itemView.findViewById(R.id.genderTextView)

    fun bind(profile: Profile) {
        nameTextView.text = profile.name
        ageTextView.text = profile.age.toString()
        genderTextView.text = profile.gender
    }
}
