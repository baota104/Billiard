package com.example.billiard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    val username: String, // Mình đổi tên biến thành username luôn cho chuẩn logic

    @SerializedName("password")
    val password: String
)

