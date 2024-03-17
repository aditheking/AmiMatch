package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx



class Profile_Activity : AppCompatActivity() {
    private val mContext: Context = this@Profile_Activity
    private var imagePerson: ImageView? = null
    private var name: TextView? = null
    private val userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: create the page")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val mPulsator: PulsatorLayout = findViewById(R.id.pulsator)
        mPulsator.start()
        setupTopNavigationView()
        imagePerson = findViewById<ImageView>(R.id.circle_profile_image)
        name = findViewById<TextView>(R.id.profile_name)
        val edit_btn = findViewById<ImageButton>(R.id.edit_profile)
        edit_btn.setOnClickListener {
            val intent = Intent(
                this@Profile_Activity,
                EditProfileActivity::class.java
            )
            startActivity(intent)
        }
        val settings = findViewById<ImageButton>(R.id.settings)
        settings.setOnClickListener {
            val intent = Intent(
                this@Profile_Activity,
                SettingsActivity::class.java
            )
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: resume to the page")
    }

    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx = findViewById<BottomNavigationViewEx>(R.id.topNavViewBar)
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu = tvEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    companion object {
        private const val TAG = "Profile_Activity"
        private const val ACTIVITY_NUM = 0
        var active = false
    }
}
