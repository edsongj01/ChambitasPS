package com.pds.chambitasps.ui.configuracion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.fragment_cambiocontra.view.*
import kotlinx.android.synthetic.main.fragment_configuracion.view.*

class CambiocontraFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View = inflater.inflate(R.layout.fragment_cambiocontra, container, false)

        val guardarContra = Navigation.createNavigateOnClickListener(R.id.action_cambiocontraFragment_to_nav_configuracion)
        root.btnContraG.setOnClickListener {
            guardarContra.onClick(it)
        }

        return root
    }


}
