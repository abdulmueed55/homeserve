package com.example.homeserv.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.homeserv.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (SessionManager(this).getUserId() > 0) DashboardActivity::class.java else AuthActivity::class.java
            startActivity(Intent(this, next)); finish()
        }, 2000)
    }
}
