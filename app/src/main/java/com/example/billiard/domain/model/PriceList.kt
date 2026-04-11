package com.example.billiard.domain.model

data class PriceList(
    val id: Int,
    val startTime: String, // Có thể format dạng "HH:mm:ss"
    val endTime: String,   // Có thể format dạng "HH:mm:ss"
    val unitPrice: Double,
    val tableType: String  // Ví dụ: "POOL", "CAROM", "SNOOKER"
)