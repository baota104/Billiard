package com.example.billiard.domain.request

// Class này thuộc tầng Domain
data class UpdateEmployeeParam(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: String,
    val isActive: Boolean
)