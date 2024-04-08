package com.mini.amimatch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Confession(
    var userId: String = "",
    var confessionText: String = "",
    var timestamp: Long = 0,
    val date: String? = null,
    val isDateHeader: Boolean = false
) {
    constructor() : this("", "", 0)

    fun getFormattedTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
}
