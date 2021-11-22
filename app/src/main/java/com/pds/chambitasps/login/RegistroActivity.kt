package com.pds.chambitasps.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.activity_registro.*

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        btnBack.setOnClickListener {
            super.onBackPressed()
        }

    }
}