package com.mini.amimatch

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.mini.amimatch.Profile_Activity

object TopNavigationViewHelper {
    private const val TAG = "TopNavigationViewHelper"
    fun setupTopNavigationView(tv: BottomNavigationViewEx?) {
        Log.d(TAG, "setupTopNavigationView: setting up navigationview")
    }

    fun enableNavigation(context: Context, view: BottomNavigationViewEx) {
        view.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_profile -> {
                    val intent2 = Intent(
                        context,
                        Profile_Activity::class.java
                    )
                    context.startActivity(intent2)
                }

                R.id.ic_main -> {
                    val intent1 = Intent(
                        context,
                        MainActivity::class.java
                    )
                    context.startActivity(intent1)
                }

                R.id.ic_matched -> {
                    val intent3 = Intent(
                        context,
                        Matched_Activity::class.java
                    )
                    context.startActivity(intent3)
                }
            }
            false
        }
    }
}

