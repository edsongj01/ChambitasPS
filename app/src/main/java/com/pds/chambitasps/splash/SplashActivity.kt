package com.pds.chambitasps.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.pds.chambitasps.MainActivity
import com.pds.chambitasps.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        launchSplash()
    }


    fun launchSplash(){
        Handler().postDelayed(Runnable{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },3000)
    }
}