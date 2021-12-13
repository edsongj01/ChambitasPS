package com.pds.chambitasps.body

import android.app.Service
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pds.chambitasps.R
import com.pds.chambitasps.util.Constants.Companion.SERVICE_CANCELADO
import com.pds.chambitasps.util.Constants.Companion.SERVICE_EN_CAMINO
import com.pds.chambitasps.util.Constants.Companion.SERVICE_EN_ESPERA
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_pedirservicio.*
import kotlinx.android.synthetic.main.fragment_pedirservicio.view.*

class PedirservicioFragment : Fragment() {

    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var id_service = ""
    private var service: Service? = null
    private var regular_user: RegularUser? = null

    @Parcelize
    data class Service(
        var estado: String,
        var servicioActivo: Boolean,
        var calificacion: Long,
        var fecha_servicio: String,
        var user_regular: String,
        var lat: Double,
        var lng: Double,
        var service: String,
        var direction: String
    ) : Parcelable

    @Parcelize
    data class RegularUser(
        var email: String,
        var name: String,
        var phone: String,
        var type: String
    ) : Parcelable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{ bundle ->
            id_service = bundle.getString("id_service")!!
            Log.d("PedirServicio", "Id de servicio: $id_service")
        }

        auth = Firebase.auth

        if (id_service.isNotEmpty()) {
            db.collection("servicios").document(id_service).get()
                .addOnSuccessListener { document ->
                    Log.d("PedirServicio", "Servicio entro")
                    service = Service(
                        document["estado"].toString(),
                        document["servicioActivo"] as Boolean,
                        document["calificacion"] as Long,
                        document["fecha_servicio"].toString(),
                        document["user_regular"].toString(),
                        document["lat"] as Double,
                        document["lng"] as Double,
                        document["service"].toString(),
                        document["direction"].toString()
                    )
                    db.collection("usuarios").document(service!!.user_regular).get()
                        .addOnSuccessListener { user_document ->
                            Log.d("PedirServicio", "Usuario regular entro")
                            regular_user = RegularUser(
                                user_document["email"].toString(),
                                user_document["name"].toString(),
                                user_document["phone"].toString(),
                                user_document["type"].toString(),
                            )

                            textView40.setText(regular_user!!.name)
                            textView41.setText(regular_user!!.phone)
                            textView42.setText(service!!.service)
                            textView43.setText(service!!.direction)
                        }
                        .addOnFailureListener {
                            Log.e("PedirServicio", "Error al obtener el usuario del servicio")
                        }
                }
                .addOnFailureListener {
                    Log.e("PedirServicio", "Error al obtener el documento de servicio")
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_pedirservicio, container, false)


        root.btnAceptarServicio.setOnClickListener { view ->

            val user = auth.currentUser

            val updates = hashMapOf<String, Any>(
                "estado" to SERVICE_EN_CAMINO,
                "user_prestador" to user!!.uid
            )

            db.collection("servicios").document(id_service).update(updates)
                .addOnSuccessListener {
                    Log.d("PedirServicio", "Servicio Aceptado")
                    val nav = Navigation.createNavigateOnClickListener(R.id.action_pedirservicioFragment_to_aceptacionservicioFragment,
                        bundleOf(
                            "id_service" to id_service,
                            "service" to service,
                            "regular_user" to regular_user
                        ))
                    nav.onClick(view)
                }
                .addOnFailureListener {
                    Log.d("PedirServicio", "No se pudo aceptar el servicio", it)
                }
        }


        root.btnCancelarServicio.setOnClickListener { view ->

            val updates = hashMapOf<String, Any>(
                "estado" to SERVICE_EN_ESPERA
            )

            db.collection("servicios").document(id_service).update(updates)
                .addOnSuccessListener {
                    Log.d("PedirServicio", "Servicio rechazado")
                    val cancelar = Navigation.createNavigateOnClickListener(R.id.action_pedirservicioFragment_to_nav_home)
                    cancelar.onClick(view)
                }
                .addOnFailureListener {
                    Log.d("PedirServicio", "No se pudo cancelar el servicio", it)
                }
        }

        return root
    }


}