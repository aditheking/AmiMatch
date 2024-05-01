package com.mini.amimatch

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.FirebaseFirestore

class LikeMatchActivity : AppCompatActivity() {
    private val TAG = "LikeMatchActivity"
    private val matchList = ArrayList<Users>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: LikeMatchUserAdapter
    private val db = FirebaseFirestore.getInstance()
    private var userList = listOf<Users>()
    private var currentIndex = 0
    private var animationView: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_match)

        recyclerView = findViewById(R.id.match_recycler_view)
        mAdapter = LikeMatchUserAdapter(matchList, this, db)

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        fetchAllUsers()

        findViewById<Button>(R.id.generate_button).setOnClickListener {
            if (animationView == null) {
                displayAnimation()
            }
        }
    }

    private fun fetchAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList =
                    result.documents.mapNotNull { it.toObject(Users::class.java) }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching users data", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error fetching users data", exception)
            }
    }

    private fun generateRandomMatch() {
        userList = userList.shuffled()

        if (currentIndex < userList.size) {
            val currentUser = userList[currentIndex]

            matchList.clear()

            matchList.add(currentUser)

            mAdapter.notifyDataSetChanged()

            currentIndex++
        } else {
            currentIndex = 0

            Toast.makeText(this, "All users have been shown", Toast.LENGTH_SHORT).show()
        }

        Log.d(TAG, "Generated random match: $matchList")
    }

    private fun displayAnimation() {
        // Display animation
        animationView = LottieAnimationView(this)
        animationView?.setAnimation("hearts.json")
        animationView?.setBackgroundColor(getColor(android.R.color.transparent))
        animationView?.playAnimation()
        animationView?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                animationView?.visibility = android.view.View.GONE
                generateRandomMatch()
                animationView?.removeAllAnimatorListeners()
                animationView = null
            }

            override fun onAnimationCancel(animation: Animator) {
                animationView?.visibility = android.view.View.GONE
                animationView?.removeAllAnimatorListeners()
                animationView = null
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })

        val rootLayout: ViewGroup = findViewById(R.id.root_layout)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        animationView?.layoutParams = layoutParams
        rootLayout.addView(animationView)
    }
}
