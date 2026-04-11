package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateEmployeeRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("role") val role: String
)