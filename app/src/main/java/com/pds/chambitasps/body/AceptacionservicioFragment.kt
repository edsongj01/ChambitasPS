package com.pds.chambitasps.body

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pds.chambitasps.MainActivity
import com.pds.chambitasps.MenuActivity
import com.pds.chambitasps.R
import com.pds.chambitasps.util.Constants
import com.pds.chambitasps.util.Constants.Companion.SERVICE_CANCELADO
import com.pds.chambitasps.util.Constants.Companion.SERVICE_EN_CAMINO
import com.pds.chambitasps.util.Constants.Companion.SERVICE_EN_ESPERA
import com.pds.chambitasps.util.Constants.Companion.SERVICE_TERMINADO
import com.pds.chambitasps.util.ForegroundLocationService
import kotlinx.android.synthetic.main.fragment_aceptacionservicio.*
import kotlinx.android.synthetic.main.fragment_aceptacionservicio.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception

class AceptacionservicioFragment : Fragment() {

    lateinit var nMap: GoogleMap
    lateinit var receiver: BroadcastReceiver
    lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    lateinit var root: View
    private lateinit var registration: ListenerRegistration

    var idService = ""

    private inner class MyLocationReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val location: Location? = intent.getParcelableExtra<Location>(Constants.EXTRA_LOCATION)
            if (location != null) {
                Log.d("Location receiver", "(${location.latitude}, ${location.longitude})")
                val myLocation = LatLng(location.latitude, location.longitude)
                if (::nMap.isInitialized) {
                    //moveMyCamera(myLocation)
                    val updates = hashMapOf<String, Any>(
                        "latitude" to myLocation.latitude,
                        "longitude" to myLocation.longitude,
                    )
                    val user = auth.currentUser
                    user?.let {
                        val uid = it.uid
                        db.collection("usuarios").document(uid).update(updates)
                            .addOnCompleteListener {
                                Log.d("RegistroUser", "Localizacion actualizada")
                            }
                            .addOnFailureListener { e ->
                                Log.e("RegistroUser", "Error al cambiar la localizacion del usuario", e)
                            }
                    }
                }
            } else {
                Log.d("ELse", "Entro aqui")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        receiver = MyLocationReceiver()
        LocalBroadcastManager.getInstance(requireActivity().applicationContext)
            .registerReceiver(receiver, IntentFilter(Constants.ACTION_BROADCAST))

        startLocationService()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_aceptacionservicio, container, false)


        root.btnCancelarServicio.setOnClickListener {

            val updates = hashMapOf<String, Any>(
                "estado" to SERVICE_CANCELADO
            )

            val user = auth.currentUser

            db.collection("servicios")
                .whereEqualTo("user_prestador", user!!.uid)
                .whereEqualTo("servicioActivo", true)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        idService = document.id
                    }

                    db.collection("servicios").document(idService).update(updates)
                }
        }

        root.btnTerminarServicio.setOnClickListener {

            val updates = hashMapOf<String, Any>(
                "estado" to SERVICE_TERMINADO
            )

            val user = auth.currentUser

            db.collection("servicios")
                .whereEqualTo("user_prestador", user!!.uid)
                .whereEqualTo("servicioActivo", true)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        idService = document.id
                    }

                    db.collection("servicios").document(idService).update(updates)
                }
            /*
            val terminar = Navigation.createNavigateOnClickListener(R.id.action_aceptacionservicioFragment_to_nav_home)
            terminar.onClick(it)
            */
        }


        root.btnChat.setOnClickListener {
            if (idService.isNotEmpty()) {
                val chat = Navigation.createNavigateOnClickListener(R.id.action_aceptacionservicioFragment_to_chatFragment,
                    bundleOf("idService" to idService)
                )
                chat.onClick(it)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapaceptarservicio) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        registration.remove()
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        nMap = googleMap
        nMap.isMyLocationEnabled = true

        if (ForegroundLocationService.myLocation != null) {
            moveMyCamera(LatLng(
                ForegroundLocationService.myLocation!!.latitude,
                ForegroundLocationService.myLocation!!.longitude))
        }

        getServiceStatus()
    }

    private fun getServiceStatus() {
        val user = auth.currentUser

        var markerOptions: MarkerOptions
        var mapMarker: Marker? = null

        registration = db.collection("servicios")
            .whereEqualTo("user_prestador", user!!.uid)
            .whereEqualTo("servicioActivo", true)
            .limit(1)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.d("ServiceStatus", "Error al escuchar el documento", error)
                }
                for (doc in document!!) {
                    when (doc.data["estado"]) {
                        SERVICE_EN_CAMINO -> {
                            Log.d("ServiceStatus", "En camino")
                            Log.d("ServiceStatus", "Destino: ${doc.data["lat"]} ${doc.data["lng"]}")

                            idService = doc.id

                            val destino_location = LatLng(
                                doc.data["lat"] as Double,
                                doc.data["lng"] as Double,
                            )
                            markerOptions = MarkerOptions()
                                .position(destino_location)
                                .title("Destino")

                            mapMarker = nMap.addMarker(markerOptions)

                            db.collection("usuarios").document(doc.data["user_regular"].toString()).get()
                                .addOnSuccessListener { user ->
                                    if (user.exists() && user != null) {
                                        textView30.setText(doc.data["service"].toString())
                                        textView15.setText(doc.data["estado"].toString())
                                        textView22.setText(doc.data["direction"].toString())
                                        textView27.setText(user.data!!["name"].toString())
                                        textView44.setText(user.data!!["phone"].toString())
                                    }
                                }
                                .addOnFailureListener {
                                    Log.d("ServiceStatus", "No se pudo obtener el usuario cliente", it)
                                }
                        }
                        SERVICE_TERMINADO -> {
                            Log.d("ServiceStatus", "Terminado")
                            //findNavController(requireActivity(), R.id.nav_host_fragment_content_menu).navigate(R.id.action_aceptacionservicioFragment_to_nav_home)
                            val pendingIntentFragment = NavDeepLinkBuilder(requireActivity().applicationContext)
                                .setComponentName(MenuActivity::class.java)
                                .setGraph(R.navigation.mobile_navigation)
                                .setDestination(R.id.nav_home)
                                .createPendingIntent()
                            pendingIntentFragment.send()
                        }
                        SERVICE_CANCELADO -> {
                            Log.d("ServiceStatus", "Cancelado")
                            val updates = hashMapOf<String, Any>(
                                "servicioActivo" to false
                            )
                            db.collection("servicios").document(doc.id).update(updates)
                            //findNavController(requireActivity(), R.id.nav_host_fragment_content_menu).navigate(R.id.action_aceptacionservicioFragment_to_nav_home)
                            val pendingIntentFragment = NavDeepLinkBuilder(requireActivity().applicationContext)
                                .setComponentName(MenuActivity::class.java)
                                .setGraph(R.navigation.mobile_navigation)
                                .setDestination(R.id.nav_home)
                                .createPendingIntent()
                            pendingIntentFragment.send()
                        }
                    }
                }
            }
    }

    private fun moveMyCamera(myPosition: LatLng) {
        try {
            nMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition))
            nMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 19f))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager: ActivityManager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (ForegroundLocationService::class.java.name.equals(service.service.className)) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()){
            val intent = Intent(context, ForegroundLocationService::class.java)
            intent.action = Constants.ACTION_START_LOCATION_SERVICE
            requireActivity().startService(intent)
        }
    }

}