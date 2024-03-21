package com.mini.amimatch

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
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
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://amimatch-48b17.appspot.com")
        val updatesReference = storageReference.child("updates")
        val apkReference = updatesReference.child("app-release.apk")

        apkReference.downloadUrl
            .addOnSuccessListener { uri ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.type = "application/vnd.android.package-archive"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }.addOnFailureListener { exception ->
            Toast.makeText(this@SettingsActivity, "Failed to download update", Toast.LENGTH_SHORT).show()
        }
    }

    fun Logout(view: View?) {
        startActivity(Intent(applicationContext, Login::class.java))
        finish()
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }
}
