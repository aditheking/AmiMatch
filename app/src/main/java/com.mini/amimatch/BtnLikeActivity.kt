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

class BtnLikeActivity : AppCompatActivity() {
    private val mContext: Context = this@BtnLikeActivity
    private var like: ImageView? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btn_like)
        setupTopNavigationView()
        like = findViewById(R.id.like) as? ImageView
        if (like != null) {
            val intent: Intent = getIntent()
            val profileUrl = intent.getStringExtra("url")
            when (profileUrl) {
                "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman).into(like!!)
                "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(like!!)
                else -> Glide.with(mContext).load(profileUrl).into(like!!)
            }
        }

        Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val mainIntent: Intent = Intent(this@BtnLikeActivity, MainActivity::class.java)
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
        private const val TAG = "BtnLikeActivity"
        private const val ACTIVITY_NUM = 1
    }
}
