package com.pds.chambitasps.ui.home

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.pds.chambitasps.MainActivity
import com.pds.chambitasps.R
import com.pds.chambitasps.body.AceptacionservicioFragment
import com.pds.chambitasps.body.PedirservicioFragment
import com.pds.chambitasps.util.Constants
import com.pds.chambitasps.util.Constants.Companion.ACTION_BROADCAST
import com.pds.chambitasps.util.Constants.Companion.EXTRA_LOCATION
import com.pds.chambitasps.util.Constants.Companion.SERVICE_EN_CAMINO
import com.pds.chambitasps.util.Constants.Companion.SERVICE_TERMINADO
import com.pds.chambitasps.util.ForegroundLocationService
import com.pds.chambitasps.util.LocationService
import kotlinx.android.synthetic.main.fragment_aceptacionservicio.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_pedirservicio.*
import kotlinx.android.synthetic.main.fragment_pedirservicio.contenedor_solicitud2
import java.lang.Exception

class HomeFragment : Fragment() {

    lateinit var nMap: GoogleMap
    lateinit var receiver: BroadcastReceiver
    lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    var isUSerMovingCamera: Boolean = false

    private inner class MyLocationReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val location: Location? = intent.getParcelableExtra<Location>(EXTRA_LOCATION)
            if (location != null) {
                Log.d("Location receiver", "(${location.latitude}, ${location.longitude})")
                val myLocation = LatLng(location.latitude, location.longitude)
                if (::nMap.isInitialized) {
                    if (!isUSerMovingCamera) moveMyCamera(myLocation)
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
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startLocationService()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        auth = Firebase.auth

        db.collection("servicios")
            .whereEqualTo("user_prestador", auth.uid)
            .whereEqualTo("servicioActivo", true)
            .whereEqualTo("estado", SERVICE_EN_CAMINO)
            .limit(1)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && !document.isEmpty) {
                    Log.d("ServicioActivo", "Se encontro un servicio en proceso")
                    Navigation.findNavController(root).navigate(R.id.action_nav_home_to_aceptacionservicioFragment)
                }
            }
            .addOnFailureListener {
                Log.d("ServicioActivo", "No se pudo encontrar el servicio solicitado", it)
            }
        val nav = Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_pedirservicioFragment)
        root.btnCentrarUbi.setOnClickListener {
            nav.onClick(it)
        }


        root.btnCentrarUbi.setOnClickListener {
            println("Centrar")
            moveMyCamera(LatLng(
                ForegroundLocationService.myLocation!!.latitude,
                ForegroundLocationService.myLocation!!.longitude))
            isUSerMovingCamera = false
        }

        // Dialogo Inicial
        val bottomSheetDialog = BottomSheetDialog(
            requireContext(), R.style.BottomSheetDialogTheme
        )
        val bottomSheetView: View = LayoutInflater.from(context)
            .inflate(
                R.layout.dialog_bienvenido,
                root.findViewById<View>(R.id.dialog_bienvenido) as LinearLayout?
            )
        bottomSheetView.findViewById<View>(R.id.btngracias).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        //bottomSheetDialog.show()

        receiver = MyLocationReceiver()
        LocalBroadcastManager.getInstance(requireActivity().applicationContext)
            .registerReceiver(receiver, IntentFilter(ACTION_BROADCAST))

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        nMap = googleMap
        nMap.isMyLocationEnabled = true

        nMap.setOnCameraMoveListener {
            isUSerMovingCamera = true
        }

        if (ForegroundLocationService.myLocation != null) {
            moveMyCamera(LatLng(
                ForegroundLocationService.myLocation!!.latitude,
                ForegroundLocationService.myLocation!!.longitude))
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