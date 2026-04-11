package com.example.billiard.core.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("billiard_prefs", Context.MODE_PRIVATE)

    fun saveAuthData(accessToken: String, refreshToken: String, role: String, id: Int) {
        prefs.edit().apply {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            putString("USER_ROLE", role)
            putInt("USER_ID", id)
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString("ACCESS_TOKEN", null)
    fun getUserRole(): String? = prefs.getString("USER_ROLE", null)
    
    // Thêm hàm lấy Employee ID để truyền vào request Mở bàn
    fun getEmployeeId(): Int? {
        val id = prefs.getInt("USER_ID", -1)
        return if (id != -1) id else null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}