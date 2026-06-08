package com.example.homeserv.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeserv.R
import com.example.homeserv.adapters.OfferAdapter
import com.example.homeserv.data.Roles
import com.example.homeserv.data.User
import com.example.homeserv.db.DBHelper
import com.google.android.material.button.MaterialButton

class DashboardActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var adapter: OfferAdapter
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_dashboard)
        db = DBHelper(this); user = db.getUserById(SessionManager(this).getUserId())
        if (user == null) { startActivity(Intent(this, AuthActivity::class.java)); finish(); return }
        findViewById<TextView>(R.id.tvGreeting).text = getString(R.string.hello_user, user!!.name)
        val canBook = user!!.role == Roles.CUSTOMER || user!!.role == Roles.ADMIN
        adapter = OfferAdapter(emptyList(), canBook) { offer ->
            startActivity(Intent(this, BookingConfirmationActivity::class.java).putExtra(BookingConfirmationActivity.EXTRA_OFFER_ID, offer.id))
        }
        findViewById<RecyclerView>(R.id.rvOffers).apply { layoutManager = LinearLayoutManager(this@DashboardActivity); adapter = this@DashboardActivity.adapter }
        findViewById<MaterialButton>(R.id.btnAddProvider).apply {
            visibility = if (user!!.role == Roles.PROVIDER || user!!.role == Roles.ADMIN) View.VISIBLE else View.GONE
            setOnClickListener { startActivity(Intent(this@DashboardActivity, AddProviderActivity::class.java)) }
        }
        findViewById<MaterialButton>(R.id.btnAdminBookings).apply {
            visibility = if (user!!.role == Roles.ADMIN) View.VISIBLE else View.GONE
            setOnClickListener { startActivity(Intent(this@DashboardActivity, MyBookingsActivity::class.java)) }
        }
        setupBottomNavigation(R.id.navHome); load()
    }
    override fun onResume() { super.onResume(); if (::adapter.isInitialized) load() }
    private fun load() {
        val counts = db.getDashboardCounts()
        findViewById<TextView>(R.id.tvTotalOffers).text = counts.totalOffers.toString()
        findViewById<TextView>(R.id.tvTotalProviders).text = counts.totalProviders.toString()
        findViewById<TextView>(R.id.tvTotalBookings).text = counts.totalBookings.toString()
        adapter.updateData(db.getOffers())
    }
}
