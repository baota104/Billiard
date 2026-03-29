package com.example.billiard.core.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("billiard_prefs", Context.MODE_PRIVATE)

    fun saveAuthData(accessToken: String, refreshToken: String, role: String) {
        prefs.edit().apply {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            putString("USER_ROLE", role)
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString("ACCESS_TOKEN", null)
    fun getUserRole(): String? = prefs.getString("USER_ROLE", null)
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}