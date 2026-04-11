package com.example.billiard.domain.model

import java.util.Date

data class CreateInvoiceParam(
    val startTime: Date? = null, 
    val endTime: Date? = null,
    val paymentMethod: String? = null,
    val serviceAmount: Double = 0.0,
    val productAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val voucherId: Int? = null,
    val employeeId: Int,
    val billiardTableId: Int
)