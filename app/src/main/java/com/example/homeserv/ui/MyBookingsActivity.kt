package com.example.homeserv.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeserv.R
import com.example.homeserv.adapters.BookingAdapter
import com.example.homeserv.data.Roles
import com.example.homeserv.data.User
import com.example.homeserv.db.DBHelper

class MyBookingsActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var adapter: BookingAdapter
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_my_bookings)
        db = DBHelper(this); user = db.getUserById(SessionManager(this).getUserId())
        if (user == null) { startActivity(Intent(this, AuthActivity::class.java)); finish(); return }
        findViewById<TextView>(R.id.tvBookingsTitle).text = if (user!!.role == Roles.ADMIN) getString(R.string.all_bookings) else getString(R.string.my_bookings)
        adapter = BookingAdapter(emptyList(), user!!.role == Roles.PROVIDER || user!!.role == Roles.ADMIN) { booking ->
            db.markBookingCompleted(booking.id); Toast.makeText(this, R.string.booking_completed, Toast.LENGTH_SHORT).show(); load()
        }
        findViewById<RecyclerView>(R.id.rvBookings).apply { layoutManager = LinearLayoutManager(this@MyBookingsActivity); adapter = this@MyBookingsActivity.adapter }
        setupBottomNavigation(R.id.navBookings); load()
    }
    override fun onResume() { super.onResume(); if (::adapter.isInitialized) load() }
    private fun load() {
        val bookings = db.getBookingsForUser(user!!)
        findViewById<TextView>(R.id.tvBookingsEmpty).visibility = if (bookings.isEmpty()) View.VISIBLE else View.GONE
        adapter.updateData(bookings)
    }
}
