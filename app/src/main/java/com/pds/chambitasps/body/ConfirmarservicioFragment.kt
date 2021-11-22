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
import kotlinx.android.synthetic.main.fragment_home.view.*


class ConfirmarservicioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_confirmarservicio, container, false)

        val confirmar = Navigation.createNavigateOnClickListener(R.id.action_confirmarservicioFragment_to_aceptacionservicioFragment)
        root.btnConfirmarservicio.setOnClickListener {
            confirmar.onClick(it)
        }


        return root
    }

}