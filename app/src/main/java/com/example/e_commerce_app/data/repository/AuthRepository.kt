package com.example.e_commerce_app.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val _loggedIn = MutableStateFlow(prefs.getBoolean(KEY_LOGGED_IN, false))
    val loggedIn: StateFlow<Boolean> = _loggedIn.asStateFlow()

    fun userName(): String = prefs.getString(KEY_NAME, "Shopper") ?: "Shopper"
    fun userEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun signUp(name: String, email: String, password: String) {
        prefs.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email.lowercase())
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
        _loggedIn.value = true
    }

    fun login(email: String, password: String): Boolean {
        val storedEmail = prefs.getString(KEY_EMAIL, null)
        val storedPassword = prefs.getString(KEY_PASSWORD, null)
        if (storedEmail.isNullOrEmpty() || storedPassword.isNullOrEmpty()) return false
        val match = storedEmail.equals(email, ignoreCase = true) && storedPassword == password
        if (match) {
            prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
            _loggedIn.value = true
        }
        return match
    }

    fun isLoggedIn(): Boolean = _loggedIn.value

    fun logout() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply()
        _loggedIn.value = false
    }

    companion object {
        private const val PREFS = "shoply.auth"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
    }
}