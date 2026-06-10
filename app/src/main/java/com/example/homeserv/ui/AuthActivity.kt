package com.example.homeserv.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var roleSpinner: Spinner
    private lateinit var serviceTypeLabel: TextView
    private lateinit var serviceTypeSpinner: Spinner
    private lateinit var actionButton: MaterialButton
    private lateinit var title: TextView
    private lateinit var toggle: TextView

    private val roles = listOf(Roles.CUSTOMER, Roles.PROVIDER)
    private val serviceTypes = listOf("Plumber", "Electrician", "Cleaner", "Carpenter", "Painter")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_auth)
        db = DBHelper(this)
        title = findViewById(R.id.tvAuthTitle); nameLayout = findViewById(R.id.layoutName)
        nameInput = findViewById(R.id.etName); phoneInput = findViewById(R.id.etPhone)
        passwordLayout = findViewById(R.id.layoutPassword); passwordInput = findViewById(R.id.etPassword)
        roleSpinner = findViewById(R.id.spinnerRole); serviceTypeLabel = findViewById(R.id.tvServiceTypeLabel)
        serviceTypeSpinner = findViewById(R.id.spinnerServiceType)
        actionButton = findViewById(R.id.btnAuthAction); toggle = findViewById(R.id.tvToggleAuth)

        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        serviceTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, serviceTypes)

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = updateServiceTypeVisibility()
            override fun onNothingSelected(parent: AdapterView<*>?) = updateServiceTypeVisibility()
        }

        updateMode()
        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) { updatePasswordHelper(s?.toString().orEmpty()) }
        })
        actionButton.setOnClickListener { if (isRegister) register() else login() }
        toggle.setOnClickListener { isRegister = !isRegister; updateMode() }
    }

    private fun updateMode() {
        title.text = if (isRegister) getString(R.string.create_account) else getString(R.string.login_to_homeserv)
        nameLayout.visibility = if (isRegister) View.VISIBLE else View.GONE
        roleSpinner.visibility = if (isRegister) View.VISIBLE else View.GONE
        actionButton.text = if (isRegister) getString(R.string.register) else getString(R.string.login)
        toggle.text = if (isRegister) getString(R.string.already_account) else getString(R.string.no_account)
        passwordLayout.error = null
        passwordInput.setText("")
        updatePasswordHelper("")
        updateServiceTypeVisibility()
    }

    private fun updatePasswordHelper(password: String) {
        if (!isRegister) {
            passwordLayout.helperText = "Enter your password"
            passwordLayout.error = null
            return
        }
        passwordLayout.helperText = when {
            password.isBlank() -> "Password: 8-20 chars, uppercase, lowercase, number, special char, no spaces"
            validateStrongPassword(password) == null -> "Strong password ✅"
            else -> "Weak password — follow the required password rules"
        }
        if (password.isNotBlank()) passwordLayout.error = null
    }

    private fun updateServiceTypeVisibility() {
        val showServiceType = isRegister && roleSpinner.selectedItem?.toString() == Roles.PROVIDER
        serviceTypeLabel.visibility = if (showServiceType) View.VISIBLE else View.GONE
        serviceTypeSpinner.visibility = if (showServiceType) View.VISIBLE else View.GONE
    }

    private fun register() {
        val name = nameInput.text.toString().trim(); val phone = phoneInput.text.toString().trim(); val pass = passwordInput.text.toString().trim()
        if (name.isBlank() || phone.isBlank() || pass.isBlank()) return toast(getString(R.string.fill_all_fields))
        val passwordError = validateStrongPassword(pass)
        if (passwordError != null) {
            passwordLayout.error = passwordError
            passwordInput.requestFocus()
            return
        }
        val role = roleSpinner.selectedItem.toString()
        val serviceType = if (role == Roles.PROVIDER) serviceTypeSpinner.selectedItem.toString() else null
        val id = db.registerUser(name, phone, pass, role, serviceType)
        if (id > 0) {
            toast(getString(R.string.register_success))
            isRegister = false
            updateMode()
            nameInput.setText("")
            passwordInput.setText("")
        } else toast(getString(R.string.user_exists))
    }


    private fun validateStrongPassword(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters."
            password.length > 20 -> "Password must be maximum 20 characters."
            password.any { it.isWhitespace() } -> "Password cannot contain spaces."
            !password.any { it.isUpperCase() } -> "Add at least 1 uppercase letter."
            !password.any { it.isLowerCase() } -> "Add at least 1 lowercase letter."
            !password.any { it.isDigit() } -> "Add at least 1 number."
            !password.any { !it.isLetterOrDigit() } -> "Add at least 1 special character."
            else -> null
        }
    }

    private fun login() {
        val phone = phoneInput.text.toString().trim(); val pass = passwordInput.text.toString().trim()
        if (phone.isBlank() || pass.isBlank()) return toast(getString(R.string.fill_all_fields))
        val user = db.loginUser(phone, pass)
        if (user != null) { SessionManager(this).saveUser(user); startActivity(Intent(this, DashboardActivity::class.java)); finish() } else toast(getString(R.string.invalid_login))
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
