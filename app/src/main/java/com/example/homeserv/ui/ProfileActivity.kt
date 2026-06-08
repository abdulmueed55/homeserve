package com.example.homeserv.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.homeserv.R
import com.example.homeserv.db.DBHelper
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_profile)
        val session = SessionManager(this); val user = DBHelper(this).getUserById(session.getUserId())
        if (user == null) { startActivity(Intent(this, AuthActivity::class.java)); finish(); return }
        findViewById<TextView>(R.id.tvProfileName).text = user.name
        findViewById<TextView>(R.id.tvProfilePhone).text = getString(R.string.phone_format, user.phone)
        findViewById<TextView>(R.id.tvProfileRole).text = getString(R.string.role_format, user.role)
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            session.clear(); startActivity(Intent(this, AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
        setupBottomNavigation(R.id.navProfile)
    }
}
