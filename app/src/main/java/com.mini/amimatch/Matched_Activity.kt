package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class Matched_Activity : AppCompatActivity() {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matched)

        mContext = this
        setupTopNavigationView()
        searchFunc()

        recyclerView = findViewById(R.id.active_recycler_view)
        mRecyclerView = findViewById(R.id.matche_recycler_view)

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Create adapters
        adapter = ActiveUserAdapter(usersList, this, db)
        mAdapter = MatchUserAdapter(matchList, this, db)
        friendRequestsAdapter = FriendRequestsAdapter(friendRequestsList, this, db)


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

    private fun fetchFriendRequests() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val friendRequestsReceivedRef = db.collection("friend_requests").document(currentUserUid).collection("received")
            friendRequestsReceivedRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val senderId = document.id
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
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error getting sender data", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting friend requests", e)
                }
        }
    }

    private fun acceptFriendRequest(user: Users) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val acceptedFriendRequestsRef = db.collection("accepted_friend_requests").document(currentUserUid).collection("accepted")
            val newFriendRef = user.userId?.let { acceptedFriendRequestsRef.document(it) }
            if (newFriendRef != null) {
                newFriendRef.set(user)
                    .addOnSuccessListener {
                        friendRequestsList.remove(user)
                        friendRequestsAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error accepting friend request", e)
                    }
            }
        }
    }

    private fun rejectFriendRequest(user: Users) {
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


    private fun prepareActiveData() {
        var users = Users("1", "Aditya Upreti", "https://i.ibb.co/mtW3zRC/Whats-App-Image-2024-04-06-at-11-29-46.jpg", "Developer of app", "Coding", 21, 1, null, true, false, false, false, "")
        usersList.add(users)
        adapter.notifyDataSetChanged()
    }


    private fun fetchUsersData() {
        val usersCollection = db.collection("users")
        usersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val usersList = mutableListOf<Users>()
                for (document in querySnapshot.documents) {
                    val userData = document.data
                    if (userData != null) {
                        val userId = userData["userId"] as? String
                        if (userId != currentUserId) {
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
                            usersList.add(user)
                        } else {
                            Log.e(TAG, "User data is null for document ${document.id}")
                        }
                    }
                }
                generateRandomMatches(usersList)

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
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


    private fun generateRandomMatches(usersList: List<Users>) {
        matchList.clear()

        for (i in 0 until usersList.size - 1) {
            val user1 = usersList[i]
            for (j in i + 1 until usersList.size) {
                val user2 = usersList[j]
                if (user1.userId != user2.userId && haveCommonInterests(user1, user2)) {
                    matchList.add(user1)
                    matchList.add(user2)
                }
            }
        }

        for (user in usersList) {
            if (user.userId != currentUserId && !matchList.contains(user)) {
                matchList.add(user)
            }
        }

        val uniqueMatchList = matchList.distinctBy { it.userId }
        matchList.clear()
        matchList.addAll(uniqueMatchList)

        mAdapter.notifyDataSetChanged()
    }


    private fun haveCommonInterests(user1: Users, user2: Users): Boolean {
        return user1.sports == user2.sports ||
                user1.fishing == user2.fishing ||
                user1.music == user2.music ||
                user1.travel == user2.travel
    }

    private fun searchFunc() {
    }

    private fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }

    fun chat(view: View) {
        val intent = Intent(this, PrivateChatActivity::class.java)
        startActivity(intent)
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