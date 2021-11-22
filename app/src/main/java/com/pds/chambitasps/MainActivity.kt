package com.pds.chambitasps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pds.chambitasps.login.IngresaUsuarioActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnIngresar.setOnClickListener {
            startActivity(Intent(this, IngresaUsuarioActivity::class.java))
        }
    }
}