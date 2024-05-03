package com.mini.amimatch
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val ADMIN_CHANNEL_ID = "admin_channel"


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val tokenMap = hashMapOf("token" to token)
            FirebaseFirestore.getInstance().collection("tokens").document(userId)
                .set(tokenMap)
                .addOnSuccessListener {
                    Log.d("FCM", "Token saved successfully: $token")
                }
                .addOnFailureListener {
                    Log.e("FCM", "Error saving token: $it")
                }
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt(3000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }

        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_main)
            .setAutoCancel(true)
            .setSound(notificationSoundUri)

        if (remoteMessage.data.containsKey("likeNotification")) {
            // like notification
            val senderName = remoteMessage.data["senderName"] ?: "Someone"
            val notificationMessage = "$senderName liked your profile"

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder
                .setContentTitle("New Like")
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)
                // private chat notification
        } else if (remoteMessage.data.containsKey("privateChat")) {
            val senderName = remoteMessage.data["senderName"] ?: "Unknown"
            val notificationMessage = remoteMessage.data["message"]

            val intent = Intent(this, PrivateChatActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder
                .setContentTitle("$senderName sent you a private message")
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)
                // confession notification
        } else if (remoteMessage.data.containsKey("confession")) {
            val notificationMessage = remoteMessage.data["message"]

            val intent = Intent(this, ConfessionActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder
                .setContentTitle("New Confession!")
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)

                // match notification
        } else if (remoteMessage.data.containsKey("matchnotification")) {
            val senderName = remoteMessage.data["senderName"] ?: "Unknown"
            val notificationMessage = "$senderName matched with you!"

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder
                .setContentTitle("New Match")
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)
        } // group chat
        else {
            val senderName = remoteMessage.data["senderName"] ?: "Unknown"
            val notificationMessage = remoteMessage.data["message"]

            val intent = Intent(this, ChatActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder
                .setContentTitle("$senderName sent a message")
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)
        }

        notificationManager.notify(notificationID, notificationBuilder.build())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = "New notification"
        val adminChannelDescription = "Device to device notification"

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}