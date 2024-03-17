package com.mini.amimatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class ProfileAdapter(private val mContext: Context, private val resourceId: Int, private val userList: List<Users>) :
    ArrayAdapter<Users>(mContext, resourceId, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(resourceId, parent, false)
            viewHolder = ViewHolder()
            viewHolder.personPic = view.findViewById(R.id.person_image)
            viewHolder.personName = view.findViewById(R.id.person_name)
            viewHolder.imageButton = view.findViewById(R.id.videoCalBtn)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val user = getItem(position)

        val profileImageUrl = user?.profileImageUrl
        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman).into(viewHolder.personPic)
            "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(viewHolder.personPic)
            else -> Glide.with(mContext).load(profileImageUrl).into(viewHolder.personPic)
        }
        viewHolder.personName.text = user?.name
        viewHolder.imageButton.isFocusable = false

        return view!!
    }

    private class ViewHolder {
        lateinit var personPic: ImageView
        lateinit var personName: TextView
        lateinit var imageButton: ImageButton
    }
}