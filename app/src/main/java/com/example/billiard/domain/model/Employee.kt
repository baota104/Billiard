package com.example.billiard.domain.model

data class Employee(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: String,
    val imageUrl: String,
    var isActive: Boolean
) {
    // Hàm tiện ích nối tên để hiển thị trên UI
    val fullName: String
        get() = listOf(lastName, firstName).filter { it.isNotBlank() }.joinToString(" ")
}