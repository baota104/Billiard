package com.example.billiard.domain.model

data class CreateVoucherParam(
    val voucherType: String,
    val value: Double,
    val source: String,
    val status: String,
    val startTime: String,
    val endTime: String,
    val quantity: Int,
    val minimumAmount: Double,
    val maximumValue: Double? = null
)