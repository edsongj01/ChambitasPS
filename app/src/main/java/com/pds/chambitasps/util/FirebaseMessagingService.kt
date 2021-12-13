package com.pds.chambitasps.util

import android.util.Log
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pds.chambitasps.MenuActivity
import com.pds.chambitasps.R
import com.pds.chambitasps.util.Constants.Companion.NOTIFICATION_REQUEST

class FirebaseMessagingService:  FirebaseMessagingService() {

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCMS", "From: ${message.from}")

        if (message.data.isNotEmpty()) {
            Log.d("FCMS", "Datos del mensaje: ${message.data}")
            if (message.data["not_type"].equals(NOTIFICATION_REQUEST)) {
                Log.d("FCMS", "Es un servicio: ${message.data}")

                val pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setComponentName(MenuActivity::class.java)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.pedirservicioFragment)
                    .setArguments(bundleOf(
                        "id_service" to message.data["id_service"]
                    ))
                    .createPendingIntent()

                pendingIntent.send()
            } else {
                Log.d("FCMS", "Es un mensaje: ${message.data}")
            }
        }

        message.notification?.let {
            Log.d("FCMS", "Mensaje de la notificacion: ${it.body}")
        }
    }

    // Solicitud de servicio


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        Log.d("UserToken", "Nuevo token: $token")
    }

}