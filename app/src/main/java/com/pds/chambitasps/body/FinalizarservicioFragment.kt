package com.pds.chambitasps.body

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.fragment_aceptacionservicio.view.*
import kotlinx.android.synthetic.main.fragment_finalizarservicio.view.*


class FinalizarservicioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_finalizarservicio, container, false)

        val atras = Navigation.createNavigateOnClickListener(R.id.action_finalizarservicioFragment_to_nav_home)
        root.btnFinalizarServicio.setOnClickListener {
            atras.onClick(it)
        }

        return root
    }

}