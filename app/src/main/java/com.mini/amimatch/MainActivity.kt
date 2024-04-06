package com.mini.amimatch

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import com.mini.amimatch.Cards

class MainActivity : Activity() {
    private val TAG = "MainActivity"
    private val ACTIVITY_NUM = 1
    private val MY_PERMISSIONS_REQUEST_LOCATION = 123
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

        // Retrieve user data from Firestore
        usersCollection.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val userData = document.toObject(Cards::class.java)
                userData?.let {
                    rowItems.add(it)
                }
            }
            arrayAdapter = PhotoAdapter(this, R.layout.item, rowItems)
            updateSwipeCard()
            checkRowItem()
            updateLocation()
            val flingContainer = findViewById<SwipeFlingAdapterView>(R.id.frame)
            flingContainer.adapter = arrayAdapter
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting documents: ", exception)
        }
    }

    private fun addPredefinedProfiles() {
        val cardsList = listOf(
            Cards().apply {
                userId = "1"
                name = "Aditya Upreti"
                age = 21
                profileImageUrl = "https://i.ibb.co/mtW3zRC/Whats-App-Image-2024-04-06-at-11-29-46.jpg"
                bio = "Developer of APP"
                interest = "coding"
                distance = 1
            },
            Cards().apply {
                userId = "2"
                name = "Ashutosh Pandey"
                age = 20
                profileImageUrl = "https://i.ibb.co/gMfnGsc/9d2d9510-bc42-4263-b8dc-5a7831476cca.jpg"
                bio = "Hi, I'm Ashutosh"
                interest = "travelling"
                distance = 1
            },
            Cards().apply {
                userId = "3"
                name = "Hritika Pandey"
                age = 19
                profileImageUrl = "https://i.ibb.co/n3SphfW/Whats-App-Image-2024-04-06-at-11-54-12.jpg"
                bio = "Hi , This is Hritika"
                interest = "Music and movies"
                distance = 1
            }
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
                    Toast.makeText(this@MainActivity, "Location Permission Denied. You have to give permission inorder to know the user range ", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
            Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_LONG).show()
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
            startActivity(btnClick)
        }
    }

    fun likeBtn(v: View?) {
        if (rowItems.isNotEmpty()) {
            val cardItem = rowItems[0]
            val userId = cardItem.userId
            //check matches
            rowItems.removeAt(0)
            arrayAdapter.notifyDataSetChanged()
            val btnClick = Intent(mContext, BtnLikeActivity::class.java)
            btnClick.putExtra("url", cardItem.profileImageUrl)
            startActivity(btnClick)
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