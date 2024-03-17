package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class ProfileCheckinMain : AppCompatActivity() {
    private lateinit var mContext: Context
    var profileImageUrl: String? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_checkin_main)
        mContext = this

        /* ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
*/
        val profileName: TextView = findViewById<TextView>(R.id.name_main)
        val profileImage: ImageView = findViewById<ImageView>(R.id.profileImage)
        val profileBio: TextView = findViewById<TextView>(R.id.bio_beforematch)
        val profileInterest: TextView = findViewById<TextView>(R.id.interests_beforematch)
        val profileDistance: TextView = findViewById<TextView>(R.id.distance_main)
        val intent: Intent = getIntent()
        val name = intent.getStringExtra("name")
        val bio = intent.getStringExtra("bio")
        val interest = intent.getStringExtra("interest")
        val distance = intent.getIntExtra("distance", 1)
        val append = if (distance == 1) "mile away" else "miles away"
        profileDistance.text = "$distance $append"
        profileName.text = name
        profileBio.text = bio
        profileInterest.text = interest
        profileImageUrl = intent.getStringExtra("photo")
        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext).load(R.drawable.default_woman)
                .into(profileImage)

            "defaultMale" -> Glide.with(mContext).load(R.drawable.default_man).into(profileImage)
            else -> Glide.with(mContext).load(profileImageUrl).into(profileImage)
        }
    }

    fun DislikeBtn(v: View?) {
        val btnClick = Intent(mContext, BtnDislikeActivity::class.java)
        btnClick.putExtra("url", profileImageUrl)
        startActivity(btnClick)
    }

    fun LikeBtn(v: View?) {
        val btnClick = Intent(mContext, BtnLikeActivity::class.java)
        btnClick.putExtra("url", profileImageUrl)
        startActivity(btnClick)
    }
}
