package com.example.billiard.domain.model

data class Invoice(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val status: String,
    val paymentMethod: String,
    val serviceAmount: Double,
    val productAmount: Double,
    val taxAmount: Double,
    val totalAmount: Double,
    val voucherId: Int,
    val voucherCode: String,
    val employeeId: Int,
    val employeeName: String,
    val billiardTableId: Int,
    val billiardTableName: String
)