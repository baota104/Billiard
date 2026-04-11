package com.example.billiard.domain.model

data class CreatePriceListParam(
    val startTime: String,
    val endTime: String,
    val unitPrice: Double,
    val tableType: String
)