package com.example.billiard.core.utils

object Constants {
    // Địa chỉ IP của máy ảo trỏ về localhost máy tính (Spring Boot)
    const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Tên file SharedPreferences để lưu cấu hình
    const val PREF_NAME = "billiard_prefs"

    // Key lưu Token đăng nhập
    const val KEY_TOKEN = "jwt_token"
}