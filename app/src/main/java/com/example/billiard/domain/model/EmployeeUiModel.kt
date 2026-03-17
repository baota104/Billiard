package com.example.billiard.domain.model

data class EmployeeUiModel(
    val id: String,
    val name: String,
    val username: String,
    val role: String,
    val createdDate: String,
    val avatarUrl: String? = null, // Có thể có ảnh hoặc null
    val initials: String? = null,  // Chữ viết tắt (VD: TH, LM)
    var isActive: Boolean = true
)