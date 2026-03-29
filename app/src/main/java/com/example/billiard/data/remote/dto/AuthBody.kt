package com.example.billiard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthBody(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Long,
    @SerializedName("employee") val employee: EmployeeInfo
)

data class EmployeeInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("role") val role: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String
)