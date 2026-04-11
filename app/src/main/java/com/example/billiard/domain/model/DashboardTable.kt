package com.example.billiard.domain.model

data class DashboardTable(
    val id: Int,
    val name: String,
    val status: String,
    val tableType: String,
    val imageUrl: String, // Bổ sung ảnh
    val activeInvoice: ActiveInvoice?
)

data class ActiveInvoice(
    val id: Int,
    val startAt: String,
    val employeeName: String
)