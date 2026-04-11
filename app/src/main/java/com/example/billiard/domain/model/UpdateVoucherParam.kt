package com.example.billiard.domain.model

data class UpdateVoucherParam(
    val id: Long, // ID cần thiết để gọi endpoint
    val status: String,
    val startTime: String,
    val endTime: String,
    val quantity: Int
)