package com.mini.amimatch

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat

class GPS(private val mContext: Context) : LocationListener {
    private var mLocationManager: LocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var location: Location? = null

    init {
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle the case where location permissions are not granted gracefully
            Log.e("GPS", "Location permissions not granted")
        } else {
            mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}


    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist *= 1609.344
        return Math.floor(dist).toInt()
    }


    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }

    companion object {
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1 // 1 minute
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
    }
}
