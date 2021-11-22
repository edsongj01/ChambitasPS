package com.pds.chambitasps.ui.acercade

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.pds.chambitasps.R
import com.pds.chambitasps.databinding.FragmentAcercaDeBinding

class AcercaDeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.fragment_acerca_de, container, false)



        return root
    }

}