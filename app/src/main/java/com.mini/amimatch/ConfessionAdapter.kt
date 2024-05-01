package com.mini.amimatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConfessionAdapter(private var confessions: List<Confession>) :
    RecyclerView.Adapter<ConfessionAdapter.ConfessionViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CONFESSION = 1
    }


    class ConfessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewConfession: TextView = itemView.findViewById(R.id.textViewConfession)
        val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_confession, parent, false)
        return ConfessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConfessionViewHolder, position: Int) {
        val confession = confessions[position]
        holder.textViewConfession.text = confession.confessionText
        holder.textViewTimestamp.text = "Confessed on ${confession.getFormattedTimestamp()}"
    }

    override fun getItemCount(): Int {
        return confessions.size
    }

    fun updateConfessions(newConfessions: List<Confession>) {
        confessions = newConfessions
        notifyDataSetChanged()
    }
}

