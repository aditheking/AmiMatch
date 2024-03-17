package com.mini.amimatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar


class SettingsActivity : AppCompatActivity() {
    private var distance: SeekBar? = null
    private var man: SwitchCompat? = null
    private var woman: SwitchCompat? = null
    private var rangeSeekBar: RangeSeekBar<Int>? = null
    private var gender: TextView? = null
    private var distance_text: TextView? = null
    private var age_rnge: TextView? = null

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
    }

    fun Logout(view: View?) {
        startActivity(Intent(applicationContext, Login::class.java))
        finish()
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }
}
