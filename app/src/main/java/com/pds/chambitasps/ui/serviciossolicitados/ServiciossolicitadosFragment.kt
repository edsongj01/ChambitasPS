package com.pds.chambitasps.ui.serviciossolicitados

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pds.chambitasps.R

class ServiciossolicitadosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.fragment_serviciossolicitados, container, false)



        return root
    }
}