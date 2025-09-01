package com.mini.amimatch

/**
 * Constants used throughout the application.
 * 
 * SECURITY NOTE: In a production environment, sensitive keys like Firebase server keys
 * should be stored securely using:
 * 1. Environment variables
 * 2. Secure key management systems (like Android Keystore)
 * 3. Remote configuration (Firebase Remote Config)
 * 4. Build-time injection from secure sources
 * 
 * Never hardcode production keys in your source code!
 */
object Constants {
    
    /**
     * Firebase Cloud Messaging Server Key
     * 
     * This should be replaced with your actual Firebase server key.
     * For production apps, consider using Firebase Admin SDK or 
     * a secure backend service to send notifications.
     * 
     * To get your server key:
     * 1. Go to Firebase Console
     * 2. Project Settings > Cloud Messaging
     * 3. Copy the Server Key
     */
    const val FCM_SERVER_KEY = "YOUR_FIREBASE_SERVER_KEY_HERE"
    
    /**
     * Firebase Cloud Messaging API URL
     */
    const val FCM_API_URL = "https://fcm.googleapis.com/fcm/send"
    
    /**
     * Application specific constants
     */
    object App {
        const val TAG = "AmiMatch"
        const val PREFS_NAME = "AmiMatchPrefs"
    }
    
    /**
     * Notification types
     */
    object NotificationTypes {
        const val MATCH_NOTIFICATION = "matchnotification"
        const val MESSAGE_NOTIFICATION = "messagenotification"
        const val FRIEND_REQUEST = "friendrequest"
    }
}