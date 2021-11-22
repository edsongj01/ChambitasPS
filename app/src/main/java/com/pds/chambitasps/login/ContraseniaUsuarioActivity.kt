package com.pds.chambitasps.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pds.chambitasps.MenuActivity
import com.pds.chambitasps.R
import com.pds.chambitasps.util.LocationService
import kotlinx.android.synthetic.main.activity_contrasenia_usuario.*

class ContraseniaUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasenia_usuario)

        btnSiguienteInCon.setOnClickListener {
            startActivity(Intent(this,MenuActivity::class.java))
            startService(Intent(this, LocationService::class.java))
        }

        btnBack.setOnClickListener {
            super.onBackPressed()
        }

    }
}