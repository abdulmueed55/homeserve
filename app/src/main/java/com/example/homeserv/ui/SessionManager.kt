package com.example.homeserv.ui

import android.content.Context
import com.example.homeserv.data.User

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("homeserv_session", Context.MODE_PRIVATE)
    fun saveUser(user: User) { prefs.edit().putInt("user_id", user.id).apply() }
    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun clear() { prefs.edit().clear().apply() }
}
