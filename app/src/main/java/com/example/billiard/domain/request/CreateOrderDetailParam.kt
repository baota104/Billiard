package com.example.billiard.domain.request

data class CreateOrderDetailParam(
    val invoiceId: Int,
    val productId: Int,
    val quantity: Int = 1,
    val price: Double,
//    val imageUrl: String?,
    val note: String? = null
)