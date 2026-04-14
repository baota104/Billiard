package com.example.billiard.domain.model

data class OrderDetail(
    val id: Int,
    val invoiceId: Int,
    val productId: Int,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?
    // Lược bỏ bớt các trường không cần thiết hiển thị lên UI cho nhẹ
)