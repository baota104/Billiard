package com.example.billiard.domain.model

import java.util.Date

data class UpdateInvoiceParam(
    val id: Int,
    val startTime: Date? = null,
    val endTime: Date? = null,
    val status: String,
    val paymentMethod: String? = null,
    val serviceAmount: Double = 0.0,
    val productAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val voucherId: Int? = null,
    val employeeId: Int,
    val billiardTableId: Int
)