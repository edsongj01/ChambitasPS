package com.pds.chambitasps.body

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.fragment_elegirdestino.view.*

class ElegirdestinoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_elegirdestino, container, false)

        val destino = Navigation.createNavigateOnClickListener(R.id.action_elegirdestinoFragment_to_pedirservicioFragment)
        root.btnConfirmardestino.setOnClickListener {
            destino.onClick(it)
        }

        return root
    }

}