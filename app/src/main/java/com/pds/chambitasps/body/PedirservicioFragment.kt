package com.pds.chambitasps.body

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.fragment_pedirservicio.view.*

class PedirservicioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_pedirservicio, container, false)

        val nav = Navigation.createNavigateOnClickListener(R.id.action_pedirservicioFragment_to_elegirdestinoFragment)
        root.etxtIngresaDestino.setOnClickListener {
            nav.onClick(it)
        }

        val nav2 = Navigation.createNavigateOnClickListener(R.id.action_pedirservicioFragment_to_confirmarservicioFragment)
        root.btnConfirmarpedirservicio.setOnClickListener {
            nav2.onClick(it)
        }

        return root
    }


}