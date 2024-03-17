package com.mini.amimatch

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class Matched_Activity : AppCompatActivity() {
    private val TAG = "Matched_Activity"
    private val ACTIVITY_NUM = 2
    private var matchList: MutableList<Users> = ArrayList()
    private var usersList: MutableList<Users> = ArrayList()
    private val mContext: Context = this@Matched_Activity
    private lateinit var recyclerView: RecyclerView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapter: ActiveUserAdapter
    private lateinit var mAdapter: MatchUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matched)
        setupTopNavigationView()
        searchFunc()

        recyclerView = findViewById(R.id.active_recycler_view)
        mRecyclerView = findViewById(R.id.matche_recycler_view)

        adapter = ActiveUserAdapter(usersList, applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
        prepareActiveData()

        mAdapter = MatchUserAdapter(matchList, applicationContext)
        mRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.adapter = mAdapter
        prepareMatchData()
    }

    private fun prepareActiveData() {
        usersList.add(
            Users(
                null,
                "S.S",
                null,
                null,
                null,
                20,
                200,
                null,
                false,
                false,
                false,
                false
            )
        )
        // Add more users as needed...
        adapter.notifyDataSetChanged()
    }

    private fun prepareMatchData() {
        matchList.add(
            Users(
                null,
                "S.S",
                null,
                null,
                null,
                20,
                200,
                null,
                false,
                false,
                false,
                false
            )
        )
        // Add more users as needed...
        mAdapter.notifyDataSetChanged()
    }

    private fun searchFunc() {
        /* Implement your search functionality here */
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
