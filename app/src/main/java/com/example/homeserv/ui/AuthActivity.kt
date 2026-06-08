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
import com.example.homeserv.data.Roles
import com.example.homeserv.db.DBHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AuthActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private var isRegister = false
    private lateinit var nameLayout: TextInputLayout
    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var roleSpinner: Spinner
    private lateinit var actionButton: MaterialButton
    private lateinit var title: TextView
    private lateinit var toggle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_auth)
        db = DBHelper(this)
        title = findViewById(R.id.tvAuthTitle); nameLayout = findViewById(R.id.layoutName)
        nameInput = findViewById(R.id.etName); phoneInput = findViewById(R.id.etPhone); passwordInput = findViewById(R.id.etPassword)
        roleSpinner = findViewById(R.id.spinnerRole); actionButton = findViewById(R.id.btnAuthAction); toggle = findViewById(R.id.tvToggleAuth)
        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf(Roles.CUSTOMER, Roles.PROVIDER))
        updateMode()
        actionButton.setOnClickListener { if (isRegister) register() else login() }
        toggle.setOnClickListener { isRegister = !isRegister; updateMode() }
    }
    private fun updateMode() {
        title.text = if (isRegister) getString(R.string.create_account) else getString(R.string.login_to_homeserv)
        nameLayout.visibility = if (isRegister) View.VISIBLE else View.GONE
        roleSpinner.visibility = if (isRegister) View.VISIBLE else View.GONE
        actionButton.text = if (isRegister) getString(R.string.register) else getString(R.string.login)
        toggle.text = if (isRegister) getString(R.string.already_account) else getString(R.string.no_account)
    }
    private fun register() {
        val name = nameInput.text.toString().trim(); val phone = phoneInput.text.toString().trim(); val pass = passwordInput.text.toString().trim()
        if (name.isBlank() || phone.isBlank() || pass.isBlank()) return toast(getString(R.string.fill_all_fields))
        val id = db.registerUser(name, phone, pass, roleSpinner.selectedItem.toString())
        if (id > 0) { toast(getString(R.string.register_success)); isRegister = false; updateMode(); nameInput.setText(""); passwordInput.setText("") } else toast(getString(R.string.user_exists))
    }
    private fun login() {
        val phone = phoneInput.text.toString().trim(); val pass = passwordInput.text.toString().trim()
        if (phone.isBlank() || pass.isBlank()) return toast(getString(R.string.fill_all_fields))
        val user = db.loginUser(phone, pass)
        if (user != null) { SessionManager(this).saveUser(user); startActivity(Intent(this, DashboardActivity::class.java)); finish() } else toast(getString(R.string.invalid_login))
    }
    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
