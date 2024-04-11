package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class BtnDislikeActivity : AppCompatActivity() {
    private val mContext: Context = this@BtnDislikeActivity
    private var dislike: ImageView? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btn_dislike)
        setupTopNavigationView()
        dislike = findViewById(R.id.dislike) as? ImageView
        if (dislike != null) {
            val intent: Intent = getIntent()
            val profileUrl = intent.getStringExtra("url")
            val profilePhotoUrl = intent.getStringExtra("photo")
            when {
                profilePhotoUrl != null -> Glide.with(mContext).load(profilePhotoUrl).into(dislike!!)
                profileUrl == "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman).into(dislike!!)
                profileUrl == "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(dislike!!)
                else -> Glide.with(mContext).load(profileUrl).into(dislike!!)
            }
        }

        Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val mainIntent: Intent = Intent(this@BtnDislikeActivity, MainActivity::class.java)
            startActivity(mainIntent)
        }.start()
    }

    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx: BottomNavigationViewEx = findViewById<BottomNavigationViewEx>(R.id.topNavViewBar)
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu: Menu = tvEx.getMenu()
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    companion object {
        private const val TAG = "BtnDislikeActivity"
        private const val ACTIVITY_NUM = 1
    }
}
