package com.example.homeserv.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.homeserv.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun AppCompatActivity.setupBottomNavigation(selectedItemId: Int) {
    val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation) ?: return
    bottomNavigation.selectedItemId = selectedItemId
    bottomNavigation.setOnItemSelectedListener { item ->
        if (item.itemId == selectedItemId) return@setOnItemSelectedListener true
        when (item.itemId) {
            R.id.navHome -> startActivity(Intent(this, DashboardActivity::class.java))
            R.id.navPostOffer -> startActivity(Intent(this, PostOfferActivity::class.java))
            R.id.navBookings -> startActivity(Intent(this, MyBookingsActivity::class.java))
            R.id.navProfile -> startActivity(Intent(this, ProfileActivity::class.java))
        }
        true
    }
}
