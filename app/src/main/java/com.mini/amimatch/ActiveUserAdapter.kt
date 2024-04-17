package com.mini.amimatch

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ActiveUserAdapter(
    private val usersList: List<Users>,
    private val activity: Activity,
    private val firestore: FirebaseFirestore

) : RecyclerView.Adapter<ActiveUserAdapter.MyViewHolder>() {

    private var storyList = mutableMapOf<String, List<String>>()
    private var currentStoryTimer: Handler? = null
    private var currentStoryIndex = 0

    companion object {
        const val GALLERY_REQUEST_CODE = 1001
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.active_user_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: Users = usersList[position]
        holder.name.text = user.name
        if (position == 0) {
            holder.btnUploadStory.visibility = View.VISIBLE
            holder.btnUploadStory.setOnClickListener {
                val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.type = "image/* video/*"
                galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                activity.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
        } else {
            holder.btnUploadStory.visibility = View.GONE
        }


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
            loadUserStories(senderId, holder)
        }

        holder.name.text = user.name
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedMediaUri = data?.data
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (selectedMediaUri != null && userId != null) {
                uploadStory(userId, selectedMediaUri)
            }
        }
    }


    fun uploadStory(userId: String, storyUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("stories")
            .child("$userId/${System.currentTimeMillis()}")

        storageRef.putFile(storyUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val storyReference = uri.toString()
                    val storyDocRef = firestore.collection("stories").document(userId)

                    storyDocRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result != null && task.result.exists()) {
                                storyDocRef.update("stories", FieldValue.arrayUnion(storyReference))
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Story uploaded successfully")
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Error updating story: $exception")
                                    }
                            } else {
                                storyDocRef.set(mapOf("stories" to listOf(storyReference)))
                                    .addOnSuccessListener {
                                        Log.d(TAG, "New story document created and uploaded successfully")
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Error creating story document: $exception")
                                    }
                            }
                        } else {
                            Log.e(TAG, "Error checking document existence: ${task.exception}")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error uploading story: $exception")
            }
    }


    private fun loadUserStories(userId: String, holder: MyViewHolder) {
        firestore.collection("stories").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    if (data != null) {
                        val stories = (data["stories"] as? List<*>)?.filterIsInstance<String>()
                        if (stories != null && stories.isNotEmpty()) {
                            storyList[userId] = stories
                            holder.storyIndicator.visibility = View.VISIBLE
                            holder.itemView.setOnClickListener {
                                startViewingStories(stories, holder)
                            }
                            return@addOnSuccessListener
                        }
                    }
                }
                holder.storyIndicator.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching user stories: $exception")
            }
    }

    private fun startViewingStories(stories: List<String>, holder: MyViewHolder) {
        currentStoryIndex = 0
        currentStoryTimer = Handler()
        currentStoryTimer?.post(object : Runnable {
            override fun run() {
                displayStory(stories[currentStoryIndex], holder)
                currentStoryIndex = (currentStoryIndex + 1) % stories.size
                currentStoryTimer?.postDelayed(
                    this,
                    3000
                )
            }
        })
    }

    private fun displayStory(storyUrl: String, holder: MyViewHolder) {
        Picasso.get().load(storyUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: CircleImageView = itemView.findViewById(R.id.aui_image)
        var name: TextView = itemView.findViewById(R.id.aui_name)
        var storyIndicator: ImageView = itemView.findViewById(R.id.story_indicator)
        var btnUploadStory: ImageButton = itemView.findViewById(R.id.btn_upload_story)
    }


    }

