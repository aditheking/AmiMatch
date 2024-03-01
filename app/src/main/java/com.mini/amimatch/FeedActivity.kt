package com.mini.amimatch

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction




class FeedActivity : AppCompatActivity(),ProfileAdapter.OnProfileSelectedListener,
    NavigationView.OnNavigationItemSelectedListener, CardStackListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: ProfileAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardStackView: CardStackView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        firestore = FirebaseFirestore.getInstance()

        cardStackView = findViewById(R.id.cardStackView)
        adapter = ProfileAdapter(this)
        cardStackView.layoutManager = CardStackLayoutManager(this)
        cardStackView.adapter = adapter

        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        fetchProfileData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                // Handle settings click
            }
            R.id.nav_sign_out -> {
                signOut()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun fetchProfileData() {
        firestore.collection("profiles").get()
            .addOnSuccessListener { documents ->
                val profiles = mutableListOf<Profile>()
                for (document in documents) {
                    val profile = document.toObject(Profile::class.java)
                    profiles.add(profile)
                }
                adapter.setProfiles(profiles)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch profile data", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onProfileSelected(profile: Profile) {
        val expandedProfileIntent = Intent(this, ExpandedProfileActivity::class.java)
        expandedProfileIntent.putExtra("profile", profile)
        startActivity(expandedProfileIntent)
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        if (direction == Direction.Right) {
            Toast.makeText(this, "Liked profile", Toast.LENGTH_SHORT).show()
        } else if (direction == Direction.Left) {
            Toast.makeText(this, "Disliked profile", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCardRewound() {
        Toast.makeText(this, "Card rewound", Toast.LENGTH_SHORT).show()
    }

    override fun onCardCanceled() {
        Toast.makeText(this, "Card Canceled", Toast.LENGTH_SHORT).show()
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Toast.makeText(this, "Card Appeared at position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Toast.makeText(this, "Card Disappeared at position $position", Toast.LENGTH_SHORT).show()
    }



}
