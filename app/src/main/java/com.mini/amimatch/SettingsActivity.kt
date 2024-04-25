package com.mini.amimatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar

class SettingsActivity : AppCompatActivity() {

    private var distance: SeekBar? = null
    private var man: SwitchCompat? = null
    private var woman: SwitchCompat? = null
    private var rangeSeekBar: RangeSeekBar<Int>? = null
    private var gender: TextView? = null
    private var distance_text: TextView? = null
    private var age_rnge: TextView? = null
    private var firestore: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var switch_private: SwitchCompat? = null
    private var switch_public: SwitchCompat? = null
    private var auth: FirebaseAuth? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<TextView>(R.id.toolbartag)
        toolbar.text = "Profile"

        val back = findViewById<ImageButton>(R.id.back)
        distance = findViewById(R.id.distance)
        man = findViewById(R.id.switch_man)
        woman = findViewById(R.id.switch_woman)
        distance_text = findViewById(R.id.distance_text)
        age_rnge = findViewById(R.id.age_range)
        rangeSeekBar = findViewById(R.id.rangeSeekbar)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val checkForUpdateButton = findViewById<Button>(R.id.check_for_update_button)
        val privacyPolicy = findViewById<TextView>(R.id.privacy_policy)
        val accountDelete = findViewById<TextView>(R.id.account_delete)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        val helpSupportButton = findViewById<Button>(R.id.help_support_button)
        val shareButton = findViewById<Button>(R.id.share_button)
        val legalButton = findViewById<Button>(R.id.legal_button)
        val licensesButton = findViewById<Button>(R.id.licenses_button)



        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        switch_private = findViewById(R.id.switch_private)
        switch_public = findViewById(R.id.switch_public)



        distance?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                distance_text?.text = "$progress Km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        man?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                man?.isChecked = true
                woman?.isChecked = false
            }
        }

        woman?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                woman?.isChecked = true
                man?.isChecked = false
            }
        }

        rangeSeekBar?.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            age_rnge?.text = "$minValue-$maxValue"
        }

        back.setOnClickListener { onBackPressed() }

        checkForUpdateButton.setOnClickListener {
            checkForUpdates()
        }
        privacyPolicy.setOnClickListener {
            showPrivacyPolicyDialog()
        }

        accountDelete.setOnClickListener {
            DeleteAccount()
        }

        logoutButton.setOnClickListener {
            Logout()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            finish()
        }

        helpSupportButton.setOnClickListener {
            openHelpSupportPage()
        }

        shareButton.setOnClickListener {
            shareApp()
        }

        legalButton.setOnClickListener {
            showLegalInformation()
        }

        licensesButton.setOnClickListener {
            showLicensesInformation()
        }

        switch_public?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switch_private?.isChecked = false
                updatePrivacySettings(isPrivate = false)
            } else {
                updatePrivacySettings(isPrivate = true)
            }
        }

        switch_private?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switch_public?.isChecked = false
                updatePrivacySettings(isPrivate = true)
            } else {
                updatePrivacySettings(isPrivate = false)
            }
        }

        retrievePrivacySettings()
    }

    private fun retrievePrivacySettings() {
        val userId = auth?.currentUser?.uid
        userId?.let {
            firestore?.collection("users")?.document(it)?.get()
                ?.addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val isPrivate = document.getBoolean("isPrivate") ?: false
                        switch_private?.isChecked = isPrivate
                        switch_public?.isChecked = !isPrivate
                    } else {
                        createPrivacySettings()
                    }
                }
                ?.addOnFailureListener { exception ->
                }
        }
    }

    private fun createPrivacySettings() {
        val userId = auth?.currentUser?.uid
        userId?.let {
            val data = hashMapOf<String, Any>(
                "isPrivate" to false,
                "isPublic" to true
            )
            firestore?.collection("users")?.document(it)
                ?.set(data)
                ?.addOnSuccessListener {
                    switch_private?.isChecked = false
                    switch_public?.isChecked = true
                }
                ?.addOnFailureListener { exception ->
                }
        }
    }


    private fun updatePrivacySettings(isPrivate: Boolean) {
        val userId = auth?.currentUser?.uid
        userId?.let {
            val data = hashMapOf<String, Any>(
                "isPrivate" to isPrivate,
                "isPublic" to !isPrivate
            )
            firestore?.collection("users")?.document(it)
                ?.update(data as Map<String, Any>)
                ?.addOnSuccessListener {
                }
                ?.addOnFailureListener { exception ->
                }
        }
    }



    private fun checkForUpdates() {
        firestore?.collection("app_info")?.document("latest_version")
            ?.get()
            ?.addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val latestVersion = document.getString("version")
                    val latestVersionCode = latestVersion?.toIntOrNull() ?: -1
                    val currentVersionCode = getVersionCode()

                    if (latestVersionCode > currentVersionCode) {
                        downloadAndInstallUpdate()
                    } else {
                        Toast.makeText(this@SettingsActivity, "No updates available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SettingsActivity, "No version info found", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this@SettingsActivity, "Failed to check for updates: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error fetching latest version", e)
            }
    }

    private fun getVersionCode(): Int {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Package name not found", e)
        }
        return -1
    }

    private fun downloadAndInstallUpdate() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val downloadIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://drive.google.com/drive/folders/1p_wBeXQqgU6CwZlSsjp6iZMPXotudgT3?usp=sharing")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (downloadIntent.resolveActivity(packageManager) != null) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                downloadIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New Update Available")
                .setContentText("Tap to download the latest version")
                .setSmallIcon(R.drawable.ic_main)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            Toast.makeText(this, "No app found to handle the download", Toast.LENGTH_SHORT).show()
        }
    }
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Update Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for update notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun openHelpSupportPage() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_FILE_NAME, "help_support.html")
        startActivity(intent)
    }

    private fun shareApp() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_FILE_NAME, "share_app.html")
        startActivity(intent)
    }

    private fun showLegalInformation() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_FILE_NAME, "legal_info.html")
        startActivity(intent)
    }

    private fun showLicensesInformation() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_FILE_NAME, "licenses_info.html")
        startActivity(intent)
    }

    private fun DeleteAccount() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_FILE_NAME, "delete_info.html")
        startActivity(intent)
    }


    private fun Logout() {
        FirebaseAuth.getInstance().signOut()
        clearLoginState()
        navigateToLoginActivity()
    }

    private fun clearLoginState() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }


    private fun showPrivacyPolicyDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Privacy Policy")

        val dialogView = layoutInflater.inflate(R.layout.dialog_privacy_policy, null)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true

        val privacyPolicyContent = getString(R.string.privacy_policy_content)
        webView.loadDataWithBaseURL(null, privacyPolicyContent, "text/html", "UTF-8", null)

        builder.setView(dialogView)

        builder.setPositiveButton("Close") { dialogInterface: DialogInterface, i: Int ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val TAG = "SettingsActivity"
        private const val CHANNEL_ID = "update_channel"
        private const val NOTIFICATION_ID = 123
    }
}
