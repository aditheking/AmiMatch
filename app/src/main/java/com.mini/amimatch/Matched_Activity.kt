package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx


class Matched_Activity : AppCompatActivity(), FriendRequestActionListener {
    private val TAG = "Matched_Activity"
    private val ACTIVITY_NUM = 2
    private val matchList = ArrayList<Users>()
    private val copyList = ArrayList<User>()
    private lateinit var mContext: Context
    private lateinit var search: EditText
    private val usersList = ArrayList<Users>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapter: ActiveUserAdapter
    private lateinit var mAdapter: MatchUserAdapter
    private lateinit var currentUserId: String
    private val friendRequestsList = ArrayList<Users>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var friendRequestsAdapter: FriendRequestsAdapter
    private val ACCEPTED_FRIEND_REQUESTS_PREF = "accepted_friend_requests"
    private val acceptedFriendRequests = HashSet<String>()
    private lateinit var sharedPreferences: SharedPreferences
    private var friendRequestsListener: ListenerRegistration? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matched)

        val floatingActionButton = findViewById<ImageView>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            openNewPage()

        }




    mContext = this
        setupTopNavigationView()
        searchFunc()

        recyclerView = findViewById(R.id.active_recycler_view)
        mRecyclerView = findViewById(R.id.matche_recycler_view)

        sharedPreferences = getSharedPreferences(ACCEPTED_FRIEND_REQUESTS_PREF, Context.MODE_PRIVATE)
        val acceptedRequestsSet = sharedPreferences.getStringSet(ACCEPTED_FRIEND_REQUESTS_PREF, HashSet<String>())
        if (acceptedRequestsSet != null) {
            acceptedFriendRequests.addAll(acceptedRequestsSet)
        }

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Create adapters
        adapter = ActiveUserAdapter(usersList, this, db)
        mAdapter = MatchUserAdapter(matchList, this, db)
        friendRequestsAdapter = FriendRequestsAdapter(friendRequestsList, this, db, this)


        val mLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val mLayoutManager1 = LinearLayoutManager(applicationContext)
        recyclerView.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = adapter
        }

        mRecyclerView.apply {
            layoutManager = mLayoutManager1
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        val mLayoutManager2 = LinearLayoutManager(this)
        val friendRequestsRecyclerView = findViewById<RecyclerView>(R.id.friend_requests_recycler_view)
        friendRequestsRecyclerView.apply {
            layoutManager = mLayoutManager2
            itemAnimator = DefaultItemAnimator()
            adapter = friendRequestsAdapter
        }

        currentUserId = getCurrentUserId()
        fetchUsersData()
        fetchActiveUsersData()
        fetchFriendRequests()

    }

    private fun openNewPage() {
        val intent = Intent(this, LikeMatchActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    private fun fetchFriendRequests() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val friendRequestsReceivedRef = db.collection("friend_requests").document(currentUserUid).collection("received")

            friendRequestsListener?.remove()

            friendRequestsListener = friendRequestsReceivedRef.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                friendRequestsList.clear()

                for (doc in snapshots!!.documents) {
                    val senderId = doc.id
                    if (!isFriendRequestAccepted(senderId)) {
                        val senderRef = db.collection("users").document(senderId)
                        senderRef.get()
                            .addOnSuccessListener { senderDocument ->
                                if (senderDocument.exists()) {
                                    val senderData = senderDocument.toObject(Users::class.java)
                                    if (senderData != null) {
                                        friendRequestsList.add(senderData)
                                        friendRequestsAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            .addOnFailureListener { ex ->
                                Log.e(TAG, "Error getting sender data", ex)
                            }
                    }
                }
            }
        }
    }

    override fun onAcceptFriendRequest(user: Users) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        user.userId?.let { acceptedFriendRequests.add(it) }
        saveAcceptedFriendRequestsToSharedPreferences()

        if (currentUserUid != null) {
            val acceptedFriendRequestsRef = db.collection("accepted_friend_requests").document(currentUserUid).collection("accepted")
            val newFriendRef = user.userId?.let { acceptedFriendRequestsRef.document(it) }
            if (newFriendRef != null) {
                val data = hashMapOf<String, Any>(
                    user.userId!! to true
                )
                newFriendRef.set(data)
                    .addOnSuccessListener {
                        val currentUserFriendRef = db.collection("accepted_friend_requests").document(user.userId!!).collection("accepted").document(currentUserUid)
                        currentUserFriendRef.set(mapOf(currentUserUid to true))
                            .addOnSuccessListener {
                                friendRequestsList.remove(user)
                                friendRequestsAdapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding current user to sender's friend list", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error accepting friend request", e)
                    }
            }
        }
    }




    private fun saveAcceptedFriendRequestsToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putStringSet(ACCEPTED_FRIEND_REQUESTS_PREF, acceptedFriendRequests)
        editor.apply()
    }

    private fun isFriendRequestAccepted(senderId: String): Boolean {
        return acceptedFriendRequests.contains(senderId)
    }


    override fun onRejectFriendRequest(user: Users) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val friendRequestsReceivedRef = db.collection("friend_requests").document(currentUserUid).collection("received")
            user.userId?.let {
                friendRequestsReceivedRef.document(it)
                    .delete()
                    .addOnSuccessListener {
                        friendRequestsList.remove(user)
                        friendRequestsAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error rejecting friend request", e)
                    }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter.handleActivityResult(requestCode, resultCode, data)
    }

    private fun fetchUsersData() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val acceptedFriendRequestsRef = db.collection("accepted_friend_requests").document(currentUserUid).collection("accepted")
            acceptedFriendRequestsRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userId = document.id
                        val userRef = db.collection("users").document(userId)
                        userRef.get()
                            .addOnSuccessListener { userDocument ->
                                if (userDocument.exists()) {
                                    val userData = userDocument.toObject(Users::class.java)
                                    if (userData != null) {
                                        matchList.add(userData)
                                        mAdapter.notifyDataSetChanged()
                                        uploadMatchListToFirestore(matchList)

                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error getting user data", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching accepted friend requests", e)
                }
        }
    }

    private fun uploadMatchListToFirestore(matchList: ArrayList<Users>) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val matchListRef = db.collection("match_lists").document(currentUserUid)
            val data = HashMap<String, Any>()
            matchList.forEachIndexed { index, user ->
                data[user.userId!!] = user
            }
            matchListRef.set(data)
                .addOnSuccessListener {
                    Log.d(TAG, "Match list uploaded successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error uploading match list", e)
                }
        }
    }




    private fun fetchActiveUsersData() {
        val usersCollection = db.collection("users")
        val activeUsersList = mutableListOf<Users>()

        usersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val userId = document.id
                    db.collection("stories").document(userId).get()
                        .addOnSuccessListener { storiesDocument ->
                            if (storiesDocument.exists()) {
                                val userData = document.data
                                if (userData != null) {
                                    val name = userData["name"] as? String
                                    val profileImageUrl = userData["profileImageUrl"] as? String
                                    val bio = userData["bio"] as? String
                                    val interest = userData["interest"] as? String
                                    val age = (userData["age"] as? Long)?.toInt() ?: 0
                                    val distance = (userData["distance"] as? Long)?.toInt() ?: 0
                                    val phoneNumber = userData["phoneNumber"] as? String
                                    val sports = userData["sports"] as? Boolean ?: false
                                    val fishing = userData["fishing"] as? Boolean ?: false
                                    val music = userData["music"] as? Boolean ?: false
                                    val travel = userData["travel"] as? Boolean ?: false
                                    val preferSex = userData["preferSex"] as? String ?: ""
                                    val dateOfBirth = userData["dateOfBirth"] as? String

                                    val user = Users(
                                        userId,
                                        name,
                                        profileImageUrl,
                                        bio,
                                        interest,
                                        age,
                                        distance,
                                        phoneNumber,
                                        sports,
                                        fishing,
                                        music,
                                        travel,
                                        preferSex,
                                        dateOfBirth
                                    )
                                    activeUsersList.add(user)
                                }
                            }
                            adapter = ActiveUserAdapter(activeUsersList, this, db)
                            recyclerView.adapter = adapter
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error fetching stories for user $userId: $exception")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }


    private fun searchFunc() {
    }

    private fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }

    fun chat(view: View) {
        val parentView = view.parent as View

        val recyclerView = parentView.parent as RecyclerView

        val clickedPosition = recyclerView.getChildViewHolder(parentView).adapterPosition

        val matchedUserId = getUserIdOfMatchedUser(clickedPosition)

        if (!matchedUserId.isNullOrEmpty()) {
            val intent = Intent(this, PrivateChatActivity::class.java).apply {
                putExtra("userId", matchedUserId)
                putExtra("currentUserId", currentUserId)
            }
            startActivity(intent)
        } else {
            Log.e(TAG, "No matched user found")
        }
    }


    private fun getUserIdOfMatchedUser(position: Int): String? {
        var matchedUserId: String? = null

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null && position >= 0 && position < matchList.size) {
            val matchedUser = matchList[position]

            if (!matchedUser.userId.isNullOrEmpty() && matchedUser.userId != currentUserUid) {
                matchedUserId = matchedUser.userId
            }
        }

        return matchedUserId
    }



    override fun onDestroy() {
        super.onDestroy()
        friendRequestsListener?.remove()
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
}