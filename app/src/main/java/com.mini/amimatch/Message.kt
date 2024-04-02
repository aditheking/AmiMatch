package com.mini.amimatch

data class Message(
    var id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    var timestamp: Long = 0

)
