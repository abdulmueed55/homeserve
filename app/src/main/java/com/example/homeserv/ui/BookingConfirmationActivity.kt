package com.example.homeserv.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeserv.R
import com.example.homeserv.data.Offer
import com.example.homeserv.data.Roles
import com.example.homeserv.data.User
import com.example.homeserv.db.DBHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class BookingConfirmationActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private var offer: Offer? = null
    private var customers = listOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_booking_confirmation)
        db = DBHelper(this)
        val user = db.getUserById(SessionManager(this).getUserId())
        offer = db.getOfferById(intent.getIntExtra(EXTRA_OFFER_ID, -1))
        if (user == null || offer == null) { finish(); return }
        if (user.role != Roles.CUSTOMER && user.role != Roles.ADMIN) { Toast.makeText(this, R.string.only_customers_book, Toast.LENGTH_SHORT).show(); finish(); return }
        findViewById<TextView>(R.id.tvConfirmTitle).text = offer!!.title
        findViewById<TextView>(R.id.tvConfirmProvider).text = getString(R.string.provider_format, offer!!.providerName)
        findViewById<TextView>(R.id.tvConfirmPrice).text = getString(R.string.price_format, offer!!.price)
        findViewById<TextView>(R.id.tvConfirmDuration).text = getString(R.string.duration_format, offer!!.duration)
        customers = db.getCustomers()
        val spinner = findViewById<Spinner>(R.id.spinnerCustomer)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, customers.map { it.name })
        val idx = customers.indexOfFirst { it.id == user.id }; if (idx >= 0) spinner.setSelection(idx)
        findViewById<MaterialButton>(R.id.btnConfirmBooking).setOnClickListener { confirm() }
    }
    private fun confirm() {
        val customer = customers.getOrNull(findViewById<Spinner>(R.id.spinnerCustomer).selectedItemPosition) ?: return toast(getString(R.string.no_customer_found))
        val notes = findViewById<TextInputEditText>(R.id.etBookingNotes).text.toString().trim()
        if (db.addBooking(offer!!.id, customer.id, notes) > 0) { toast(getString(R.string.booking_confirmed)); startActivity(Intent(this, MyBookingsActivity::class.java)); finish() } else toast(getString(R.string.something_wrong))
    }
    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    companion object { const val EXTRA_OFFER_ID = "extra_offer_id" }
}
