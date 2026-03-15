package com.example.billiard.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: EmployeeRes
)

data class EmployeeRes(
    val id: Int?,
    val keycloakId: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val role: String?,
    val imageUrl: String?,
    val isActive: Boolean?
)