package com.example.billiard.domain.model

data class UpdatePriceListParam(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val unitPrice: Double,
    val tableType: String
)