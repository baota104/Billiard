package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateEmployeeRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("role") val role: String,
    @SerializedName("isActive") val isActive: Boolean
)