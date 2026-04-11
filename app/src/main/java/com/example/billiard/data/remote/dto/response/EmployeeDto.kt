package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class EmployeeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("keycloakId") val keycloakId: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isActive") val isActive: Boolean?
)