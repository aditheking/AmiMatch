import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"
    private val db = FirebaseFirestore.getInstance()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if the message is a Firebase In-App Messaging message
        remoteMessage.data["google.c.a.e_a"].let { inAppMessageAction ->
            if (inAppMessageAction != null && inAppMessageAction == "true") {
                Log.d(TAG, "Received Firebase In-App Messaging message")
            } else {
                remoteMessage.data.isNotEmpty().let {
                    handleDataPayload(remoteMessage.data)
                }

                remoteMessage.notification?.let {
                    handleNotificationPayload(it)
                }
            }
        }
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Handle data payload
    }

    private fun handleNotificationPayload(notification: RemoteMessage.Notification) {
        // Handle notification payload
    }

    private fun saveTokenToFirestore(token: String) {
        val tokenData = hashMapOf(
            "token" to token
        )

        db.collection("tokens")
            .document("token")
            .set(tokenData)
            .addOnSuccessListener {
                Log.d(TAG, "Token successfully saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error saving token to Firestore", e)
            }
    }

    private fun fetchTokenFromFirestore() {
        // Fetch token from Firestore
        db.collection("tokens")
            .document("token")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val token = document.getString("token")
                    Log.d(TAG, "Token from Firestore: $token")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}
