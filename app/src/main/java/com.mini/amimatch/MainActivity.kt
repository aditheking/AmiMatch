package com.mini.amimatch

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import com.mini.amimatch.Cards
import java.util.Calendar

class MainActivity : Activity() {
    private val TAG = "MainActivity"
    private val ACTIVITY_NUM = 1
    private val MY_PERMISSIONS_REQUEST_LOCATION = 123
    private val MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS = 124
    private lateinit var listView: ListView
    private lateinit var rowItems: MutableList<Cards>
    private lateinit var cardFrame: FrameLayout
    private lateinit var moreFrame: FrameLayout
    private lateinit var mContext: Context
    private lateinit var arrayAdapter: PhotoAdapter
    private lateinit var mNotificationHelper: NotificationHelper
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this
        sharedPref = getSharedPreferences("com.mini.amimatch.PREFERENCES", Context.MODE_PRIVATE)

        if (!sharedPref.getBoolean("privacyDialogShown", false)) {
            showPrivacyPolicyDialog()
        } else {
            initializeApp()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS)
        }
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result
                FirebaseMessaging.getInstance().subscribeToTopic("topic_name")
                Log.d(TAG, "Subscribed to topic")
                saveTokenToFirestore(token)
            })
    }


    private fun saveTokenToFirestore(token: String?) {
        if (token != null) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val userTokenRef =
                    FirebaseFirestore.getInstance().collection("user_tokens").document(userId)
                userTokenRef.set(mapOf("token" to token))
                    .addOnSuccessListener {
                        Log.d(TAG, "Token successfully saved to Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error saving token to Firestore", e)
                    }
            }
        } else {
            Log.e(TAG, "FCM token is null")
        }
    }


    private fun showPrivacyPolicyDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Terms of Service")

        val dialogView = layoutInflater.inflate(R.layout.dialog_privacy_policy, null)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true

        val privacyPolicyContent = getString(R.string.privacy_policy_content)
        webView.loadDataWithBaseURL(null, privacyPolicyContent, "text/html", "UTF-8", null)

        builder.setView(dialogView)

        builder.setPositiveButton("Accept") { dialogInterface: DialogInterface, i: Int ->
            sharedPref.edit().putBoolean("privacyDialogShown", true).apply()

            initializeApp()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun initializeApp() {
        cardFrame = findViewById(R.id.card_frame)
        moreFrame = findViewById(R.id.more_frame)
        mNotificationHelper = NotificationHelper(this)

        val commentButton: ImageButton = findViewById(R.id.commentbtn)
        commentButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(intent)
        }

        val confessionButton: ImageButton = findViewById(R.id.confessionBtn)
        confessionButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ConfessionActivity::class.java)
            startActivity(intent)
        }


        val mPulsator = findViewById<PulsatorLayout>(R.id.pulsator)
        mPulsator.start()

        setupTopNavigationView()

        rowItems = ArrayList()
        addPredefinedProfiles()
        arrayAdapter = PhotoAdapter(this, R.layout.item, rowItems)
        updateSwipeCard()
        checkRowItem()

        fetchFirebaseProfiles()
    }


        // Initialize Firebase Firestore
        private fun fetchFirebaseProfiles() {
            val db = Firebase.firestore
            val usersCollection = db.collection("users")

            // get own profile data first
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            userId?.let { uid ->
                val userDocRef = usersCollection.document(uid)
                userDocRef.get().addOnSuccessListener { documentSnapshot ->
                    val userCard = documentSnapshot.toObject(Cards::class.java)
                    userCard?.let {
                        rowItems.add(it)

                        fetchOtherProfiles(usersCollection, userId)
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error retrieving user's own profile: ", exception)
                }
            }
        }

    private fun fetchOtherProfiles(usersCollection: CollectionReference, userId: String) {
        usersCollection.get().addOnSuccessListener { querySnapshot ->
            val fetchedProfiles = mutableListOf<Cards>()
            for (document in querySnapshot.documents) {
                val userData = document.toObject(Cards::class.java)
                userData?.let {
                    if (it.userId != userId) {
                        fetchedProfiles.add(it)
                    }
                }
            }

            fetchedProfiles.shuffle()

            rowItems.addAll(fetchedProfiles)

            arrayAdapter = PhotoAdapter(this, R.layout.item, rowItems)
            updateSwipeCard()
            checkRowItem()
            updateLocation()
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting documents: ", exception)
        }
    }



    private fun addPredefinedProfiles() {
        val cardsList = listOf(
            Cards().apply {
                userId = "1"
                name = "Ami-Match"
                age = 1
                profileImageUrl = "https://i.ibb.co/D5NJqZy/ami-match-logo-for-a-app-any-logo-and-below-that-t-9y-Z6-YEwr-S4e-Dr-Jmxkrrd-Dg-Qt-ZF2-HU8-QIqy3-W5.jpg"
                bio = ""
                interest = ""
                distance = 0
                about ="AmiMatch is a social matching app tailored for the Amity University. Users can swipe through profiles, upload photos, express interest with likes, and engage in private and group chats. Anonymous confessions provide a platform for sharing thoughts and feelings. Notifications keep users updated on matches and messages, fostering connections and interactions within the community. Developed by Aditya Upreti"
                year_semester =""
                course = ""
                school =""
            },
        )
        rowItems.addAll(cardsList)
    }


    private fun checkRowItem() {
        if (rowItems.isEmpty()) {
            moreFrame.visibility = View.VISIBLE
            cardFrame.visibility = View.GONE
        }
    }

    private fun updateLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Location Permission Denied. You have to give permission inorder to know the user range ",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendNotification1()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Notification Access Permission Denied. You have to give permission to access notifications.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun sendNotification1() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("INSTALL_CHANNEL_ID", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "INSTALL_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_main)
            .setContentTitle("Welcome to AMI-MATCH!")
            .setContentText("Explore new connections within Amity. Click ❤ to start exploring!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun updateSwipeCard() {
        val flingContainer = findViewById<SwipeFlingAdapterView>(R.id.frame)
        flingContainer.adapter = arrayAdapter
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                rowItems.removeAt(0)
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                checkRowItem()
            }

            override fun onRightCardExit(dataObject: Any) {
                checkRowItem()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Ask for more data here
            }

            override fun onScroll(scrollProgressPercent: Float) {
                val view = flingContainer.selectedView
                if (view != null) {
                    view.findViewById<View>(R.id.item_swipe_right_indicator)?.alpha = if (scrollProgressPercent < 0) -scrollProgressPercent else 0f
                    view.findViewById<View>(R.id.item_swipe_left_indicator)?.alpha = if (scrollProgressPercent > 0) scrollProgressPercent else 0f
                }
            }

        })

        flingContainer.setOnItemClickListener { _, _ ->
            if (rowItems.isNotEmpty()) {
                val cardItem = rowItems[0]
                val intent = Intent(mContext, ProfileCheckinMain::class.java).apply {
                    putExtra("userId", cardItem.userId)
                    putExtra("name", cardItem.name)
                    putExtra("bio", cardItem.bio)
                    putExtra("interest", cardItem.interest)
                    putExtra("distance", cardItem.distance)
                    putExtra("photo", cardItem.profileImageUrl)
                    putExtra("profilePhotoUrl", cardItem.profilePhotoUrl)
                    putExtra("about", cardItem.about)
                    putExtra("year_semester", cardItem.year_semester)
                    putExtra("course", cardItem.course)
                    putExtra("school", cardItem.school)
                }
                startActivity(intent)
            }
        }
    }

    fun sendNotification() {
        val nb = mNotificationHelper.getChannel1Notification(getString(R.string.app_name), getString(R.string.match_notification))
        mNotificationHelper.manager?.notify(1, nb.build())
    }

    fun dislikeBtn(v: View?) {
        if (rowItems.isNotEmpty()) {
            val cardItem = rowItems[0]
            val userId = cardItem.userId
            rowItems.removeAt(0)
            arrayAdapter.notifyDataSetChanged()
            val btnClick = Intent(mContext, BtnDislikeActivity::class.java)
            btnClick.putExtra("url", cardItem.profileImageUrl)
            btnClick.putExtra("photo", cardItem.profilePhotoUrl)
            startActivity(btnClick)
        }
    }

    private fun hasLikedToday(likedUserId: String, likingUserId: String): Boolean {
        val lastLikeTimestamp = sharedPref.getLong("$likingUserId-$likedUserId", -1)

        val calendar = Calendar.getInstance()

        val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        return lastLikeTimestamp != -1L && calendar.timeInMillis - lastLikeTimestamp < 24 * 60 * 60 * 1000 && currentDayOfYear == calendar.get(Calendar.DAY_OF_YEAR)
    }

    fun likeBtn(v: View?) {
        if (rowItems.isNotEmpty()) {
            val cardItem = rowItems[0]
            val likedUserId = cardItem.userId
            val likingUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (likingUserId != null) {
                if (!likedUserId?.let { hasLikedToday(it, likingUserId) }!!) {
                    val db = FirebaseFirestore.getInstance()
                    val likesRef = likedUserId.let { db.collection("likes").document(it) }
                    likesRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val likesData = document.data
                                if (likesData != null) {
                                    val likesBy = likesData["likedBy"] as? ArrayList<String>
                                    if (likesBy != null) {
                                        likesBy.add(likingUserId)
                                        val likeCount = likesBy.size
                                        likesData["count"] = likeCount
                                        likesRef.set(likesData)
                                            .addOnSuccessListener {
                                                Log.d(TAG, "Likes updated successfully.")
                                                rowItems.removeAt(0)
                                                arrayAdapter.notifyDataSetChanged()

                                                sharedPref.edit().putLong("$likingUserId-$likedUserId", System.currentTimeMillis()).apply()

                                                val btnClick = Intent(mContext, BtnLikeActivity::class.java)
                                                btnClick.putExtra("url", cardItem.profileImageUrl)
                                                btnClick.putExtra("photo", cardItem.profilePhotoUrl)
                                                startActivity(btnClick)
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Error updating likes: $e")
                                            }
                                    }
                                }
                            } else {
                                val newLikesData = hashMapOf(
                                    "likedBy" to arrayListOf<String>(likingUserId),
                                    "count" to 1
                                )
                                likesRef.set(newLikesData)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "New like document created successfully.")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error creating like document: $e")
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error fetching like document: $e")
                        }
                } else {
                    Toast.makeText(this@MainActivity, "You have already liked this user today.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx = findViewById<BottomNavigationViewEx>(R.id.topNavViewBar)
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu = tvEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
    }

    override fun onBackPressed() {
    }
}