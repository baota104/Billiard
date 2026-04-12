package com.example.billiard.domain.model

data class PurchaseInvoice(
    val id: Int,
    val totalAmount: Double,
    val importDate: String,
    val employeeId: Int,
    val employeeName: String,
    val details: List<PurchaseDetail>
)

data class PurchaseDetail(
    val id: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val importPrice: Double,
    val subTotal: Double,
    val imageUrl: String = "" // Trường tự định nghĩa ở Frontend để phục vụ hiển thị UI
)

data class PurchaseHistory(
    val purchaseId: Int,
    val totalPrice: Double,
    val purchaseDate: String,
    val employeeName: String
)