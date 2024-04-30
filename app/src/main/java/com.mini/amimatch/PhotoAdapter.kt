package com.mini.amimatch

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PhotoAdapter(
    private val mContext: Context,
    private val resource: Int,
    private val objects: List<Cards>
) : ArrayAdapter<Cards>(mContext, resource, objects) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val checkMarkVisibilityMap = HashMap<String, Boolean>()
    private val likeCountMap = HashMap<String, Long>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val cardItem = getItem(position)

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false)
        }

        val name = convertView!!.findViewById<TextView>(R.id.name)
        val image = convertView.findViewById<ImageView>(R.id.image)
        val checkVerified = convertView.findViewById<ImageView>(R.id.checkVerified)
        val likeCountTextView = convertView.findViewById<TextView>(R.id.like_count)

        if (likeCountMap.containsKey(cardItem?.userId)) {
            likeCountTextView.text = likeCountMap[cardItem?.userId].toString()
        } else {
            fetchLikeCount(cardItem?.userId, likeCountTextView)
        }

        if (!checkMarkVisibilityMap.containsKey(cardItem?.userId)) {
            fetchVerificationStatus(cardItem?.userId, checkVerified)
        }

        if (cardItem != null) {
            if (checkMarkVisibilityMap.containsKey(cardItem.userId)) {
                val isVisible = checkMarkVisibilityMap[cardItem.userId] ?: false
                setCheckMarkVisibility(checkVerified, isVisible)
            } else {
                if (cardItem != null) {
                    fetchVerificationStatus(cardItem.userId, checkVerified)
                }
            }
        }

        name.text = "${cardItem!!.name}, ${cardItem.age}"

        when {
            cardItem.profilePhotoUrl != null -> Glide.with(context).load(cardItem.profilePhotoUrl)
                .into(image)
            cardItem.profileImageUrl == "defaultFemale" -> Glide.with(context)
                .load(R.drawable.default_woman).into(image)
            cardItem.profileImageUrl == "defaultMale" -> Glide.with(context)
                .load(R.drawable.default_man).into(image)
            else -> Glide.with(context).load(cardItem.profileImageUrl).into(image)
        }

        return convertView
    }

    private fun fetchLikeCount(userId: String?, likeCountTextView: TextView) {
        if (userId != null) {
            val likesRef = firestore.collection("likes").document(userId)
            likesRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val likeCount = documentSnapshot.getLong("count") ?: 0
                        likeCountTextView.text = likeCount.toString()
                        likeCountMap[userId] = likeCount
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching like count: $exception")
                }
        }
    }


    private fun setCheckMarkVisibility(checkVerified: ImageView, isVisible: Boolean) {
        if (isVisible) {
            checkVerified.visibility = View.VISIBLE
        } else {
            checkVerified.visibility = View.GONE
        }
    }

    private fun fetchVerificationStatus(userId: String?, checkVerified: ImageView) {
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val isVerified = documentSnapshot.getBoolean("isVerified") ?: false
                        setCheckMarkVisibility(checkVerified, isVerified)
                        checkMarkVisibilityMap[userId] = isVerified
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching verification status: $exception")
                }
        }
    }
}
