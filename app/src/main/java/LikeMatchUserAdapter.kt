package com.mini.amimatch

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject

class LikeMatchUserAdapter(
    private val usersList: List<Users>,
    private val context: Context,
    private val firestore: FirebaseFirestore
) :
    RecyclerView.Adapter<LikeMatchUserAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.likematched_user_item, parent, false)
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
        holder.notifyButton.setOnClickListener {
            showConfirmationDialog(user)
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.mui_image)
        var name: TextView = itemView.findViewById(R.id.mui_name)
        var notifyButton: LottieAnimationView = itemView.findViewById(R.id.notify_button)
    }

    private fun showConfirmationDialog(user: Users) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Notify Matched User")
            .setMessage("Are you sure you want to notify ${user.name} that you matched with them?")
            .setPositiveButton("Yes") { dialog, _ ->
                notifyMatchedUser(user)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun notifyMatchedUser(matchedUser: Users) {
        val matchedUserId = matchedUser.userId
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null && matchedUserId != null) {
            firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener { document ->
                    val senderName = document.getString("name")
                    if (!senderName.isNullOrEmpty()) {
                        Log.d(TAG, "Sender's name: $senderName")
                        firestore.collection("user_tokens").document(matchedUserId).get()
                            .addOnSuccessListener { tokenDocument ->
                                val userToken = tokenDocument.getString("token")
                                if (!userToken.isNullOrEmpty()) {
                                    sendNotification(userToken, senderName)
                                } else {
                                    Log.e(TAG, "Token for matched user $matchedUserId not found")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Error fetching token for matched user $matchedUserId: $exception")
                            }
                    } else {
                        Log.e(TAG, "Name of the current user $currentUserId not found or is null")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching name of the current user $currentUserId: $exception")
                }
        } else {
            Log.e(TAG, "Current user ID or matched user ID is null")
        }
    }


    private fun sendNotification(userToken: String, senderName: String) {
        Log.d(TAG, "Sending notification for $senderName")

        val url = Constants.FCM_API_URL
        // TODO: Replace with secure server key management
        // For production, use Firebase Admin SDK or secure backend service
        val serverKey = Constants.FCM_SERVER_KEY
        
        if (serverKey == "YOUR_FIREBASE_SERVER_KEY_HERE") {
            Log.e(TAG, "Firebase server key not configured. Please set your server key in Constants.kt")
            return
        }

        val notificationData = JSONObject().apply {
            put("title", "New Match")
            put("body", "$senderName matched with you!")
            put("senderName", senderName)
        }

        val requestBody = JSONObject().apply {
            put("to", userToken)
            put("data", JSONObject().apply {
                put("matchnotification", notificationData)
                put("senderName", senderName)

            })
        }

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, requestBody,
            { response ->
                Log.d(TAG, "Notification sent successfully: $response")
            },
            { error ->
                Log.e(TAG, "Error sending notification: $error")
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "key=$serverKey"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    companion object {
        private const val TAG = "LikeMatchUserAdapter"
    }
}
