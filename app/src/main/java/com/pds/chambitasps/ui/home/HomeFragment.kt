package com.pds.chambitasps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pds.chambitasps.R
import com.pds.chambitasps.util.LocationService
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.btnCentrarUbi.setOnClickListener {

            val punto = LatLng(LocationService.loc.latitude, LocationService.loc.longitude)
            mMap.addMarker(MarkerOptions().position(punto).title("Yo"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(punto))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(punto,16.0f))
            println("ENTRO")
        }

        val nav = Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_pedirservicioFragment)
        root.btnBusquedaHome.setOnClickListener {
            nav.onClick(it)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}