package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class PhotoAdapter(
    private val mContext: Context,
    private val resource: Int,
    private val objects: List<Cards>
) : ArrayAdapter<Cards>(mContext, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val cardItem = getItem(position)

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false)
        }

        val name = convertView!!.findViewById<TextView>(R.id.name)
        val image = convertView.findViewById<ImageView>(R.id.image)
        val btnInfo = convertView.findViewById<ImageButton>(R.id.checkInfoBeforeMatched)

        name.text = "${cardItem!!.name}, ${cardItem.age}"

        btnInfo.setOnClickListener {
            val intent = Intent(mContext, ProfileCheckinMain::class.java).apply {
                putExtra("name", "${cardItem.name}, ${cardItem.age}")
                putExtra("photo", cardItem.profileImageUrl)
                putExtra("profilePhotoUrl", cardItem.profilePhotoUrl)
            //    putExtra("bio", cardItem.bio)
         //       putExtra("interest", cardItem.interest)
          //      putExtra("distance", cardItem.distance)
          //      putExtra("about",cardItem.about)
          //      putExtra("year_semester",cardItem.year_semester)
          //      putExtra("course",cardItem.course)
           //     putExtra("school",cardItem.school)

            }
            mContext.startActivity(intent)
        }

        when {
            cardItem.profilePhotoUrl != null -> Glide.with(context).load(cardItem.profilePhotoUrl).into(image)
            cardItem.profileImageUrl == "defaultFemale" -> Glide.with(context).load(R.drawable.default_woman).into(image)
            cardItem.profileImageUrl == "defaultMale" -> Glide.with(context).load(R.drawable.default_man).into(image)
            else -> Glide.with(context).load(cardItem.profileImageUrl).into(image)
        }


        return convertView
    }
}