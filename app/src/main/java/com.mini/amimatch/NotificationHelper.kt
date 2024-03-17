package com.mini.amimatch

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannels() {
        val channel1 =
            NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_HIGH)
        channel1.enableLights(true)
        channel1.enableVibration(true)
        channel1.lightColor = Color.GREEN
        channel1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel1.vibrationPattern = longArrayOf(0, 1000, 1000, 1000)
        Log.d("notification", "we are in create channels1 \n ")
        manager!!.createNotificationChannel(channel1)
        Log.d("notification", "we are in create channels2 \n ")
    }

    val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }

    fun getChannel1Notification(title: String?, message: String?): NotificationCompat.Builder {
        val intent = Intent(this, Matched_Activity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        Log.d("notification", "we are in getChaneel1Notification function \n ")
        return NotificationCompat.Builder(applicationContext, channel1ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(notificationIcon)
            .setAutoCancel(true)
            .setColor(resources.getColor(R.color.colorPrimary))
            .setContentIntent(pi)
    }

    fun getNotificationManager(): NotificationManager {
        if (mManager == null) {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager as NotificationManager
    }


    private val notificationIcon: Int
        get() {
            val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return if (useWhiteIcon) R.drawable.notification_app_icon else R.drawable.ic_location
        }

    companion object {
        const val channel1ID = "channel1ID"
        const val channel1Name = "channel 1"
    }
}
