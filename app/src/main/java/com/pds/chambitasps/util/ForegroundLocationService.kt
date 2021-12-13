package com.pds.chambitasps.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.pds.chambitasps.R
import com.pds.chambitasps.util.Constants.Companion.ACTION_BROADCAST
import com.pds.chambitasps.util.Constants.Companion.EXTRA_LOCATION

class ForegroundLocationService: Service() {

    companion object {
        var myLocation: Location? = null
    }

    private var lastLocationLatitude: Double? = null
    private var lastLocationLongitude: Double? = null

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult != null && locationResult.lastLocation != null) {

                val latitude = locationResult.lastLocation.latitude
                val longitude = locationResult.lastLocation.longitude

                if (lastLocationLatitude == null && lastLocationLongitude == null) {
                    Log.d("User fused location", "($latitude, $longitude)")
                    myLocation = locationResult.lastLocation

                    lastLocationLatitude = latitude
                    lastLocationLongitude = longitude

                    val intent = Intent(ACTION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, myLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                } else if (lastLocationLatitude != latitude && lastLocationLongitude != longitude) {
                    Log.d("User fused location", "($latitude, $longitude)")
                    myLocation = locationResult.lastLocation

                    lastLocationLatitude = latitude
                    lastLocationLongitude = longitude

                    val intent = Intent(ACTION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, myLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @SuppressLint("MissingPermission")
    private fun startLocationService() {
        val channelId = "location_notification_channel"
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationBuilder.setContentTitle("AP2 Location Service")
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationBuilder.setContentText("AP2 location service is running")
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setAutoCancel(false)
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    "Location Service",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.description = "This channel is used by location service"
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        val locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        startForeground(Constants.LOCATION_SERVICE_ID, notificationBuilder.build())
    }

    private fun stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            val action = intent.action
            if (action != null) {
                if (action == Constants.ACTION_START_LOCATION_SERVICE) {
                    startLocationService()
                } else if (action == Constants.ACTION_STOP_LOCATION_SERVICE) {
                    stopLocationService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}