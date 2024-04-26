package com.mini.amimatch

interface FriendRequestActionListener {
    fun onAcceptFriendRequest(user: Users)
    fun onRejectFriendRequest(user: Users)
}
