package com.example.homeserv.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeserv.R
import com.example.homeserv.data.Provider
import com.example.homeserv.data.Roles
import com.example.homeserv.data.User
import com.example.homeserv.db.DBHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PostOfferActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private var user: User? = null
    private var providers = listOf<Provider>()
    private lateinit var providerSpinner: Spinner
    private lateinit var submitButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_post_offer)
        db = DBHelper(this); user = db.getUserById(SessionManager(this).getUserId())
        if (user == null) { startActivity(Intent(this, AuthActivity::class.java)); finish(); return }
        providerSpinner = findViewById(R.id.spinnerProvider); submitButton = findViewById(R.id.btnSubmitOffer)
        if (user!!.role != Roles.PROVIDER && user!!.role != Roles.ADMIN) { findViewById<TextView>(R.id.tvPostOfferNote).text = getString(R.string.provider_only_note); submitButton.isEnabled = false }
        findViewById<MaterialButton>(R.id.btnGoAddProvider).setOnClickListener { startActivity(Intent(this, AddProviderActivity::class.java)) }
        submitButton.setOnClickListener { submitOffer() }
        setupBottomNavigation(R.id.navPostOffer)
    }
    override fun onResume() { super.onResume(); loadProviders() }
    private fun loadProviders() {
        providers = db.getProvidersForUser(user!!.id, user!!.role)
        providerSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, providers)
        findViewById<TextView>(R.id.tvProviderEmpty).visibility = if (providers.isEmpty()) View.VISIBLE else View.GONE
        submitButton.isEnabled = providers.isNotEmpty() && (user!!.role == Roles.PROVIDER || user!!.role == Roles.ADMIN)
    }
    private fun submitOffer() {
        val provider = providerSpinner.selectedItem as? Provider
        val title = findViewById<TextInputEditText>(R.id.etOfferTitle).text.toString().trim()
        val desc = findViewById<TextInputEditText>(R.id.etOfferDescription).text.toString().trim()
        val price = findViewById<TextInputEditText>(R.id.etOfferPrice).text.toString().toDoubleOrNull()
        val duration = findViewById<TextInputEditText>(R.id.etOfferDuration).text.toString().trim()
        if (provider == null || title.isBlank() || desc.isBlank() || price == null || duration.isBlank()) return toast(getString(R.string.fill_all_fields))
        if (db.addOffer(provider.id, title, desc, price, duration) > 0) { toast(getString(R.string.offer_posted)); finish() } else toast(getString(R.string.something_wrong))
    }
    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
