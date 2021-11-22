package com.pds.chambitasps.body

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.fragment_aceptacionservicio.view.*
import kotlinx.android.synthetic.main.fragment_confirmarservicio.view.*

class AceptacionservicioFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_aceptacionservicio, container, false)

        val cancelar = Navigation.createNavigateOnClickListener(R.id.action_aceptacionservicioFragment_to_nav_home)
        root.btnCancelarServicio.setOnClickListener {
            cancelar.onClick(it)
        }

        val chat = Navigation.createNavigateOnClickListener(R.id.action_aceptacionservicioFragment_to_chatFragment)
        root.btnChat.setOnClickListener {
            chat.onClick(it)
        }

        val finalizar = Navigation.createNavigateOnClickListener(R.id.action_aceptacionservicioFragment_to_finalizarservicioFragment)
        root.imageView7.setOnClickListener {
            finalizar.onClick(it)
        }

        return root
    }

}