package com.example.billiard.domain.model

data class CreateVoucherParam(
    val voucherType: String,// CASH hoặc PERCENTAGE
    val value: Double,
    val source: String,//AI hoặc MANUAL
    val status: String,// ACTIVE , INACTIVE,EXPIRED
    val startTime: String,
    val endTime: String,
    val quantity: Int,
    val minimumAmount: Double,
    val maximumValue: Double? = null
)