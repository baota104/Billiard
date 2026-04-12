package com.example.billiard.domain.model

data class ReceiptItemUiModel(
    val productId: Int,
    val name: String,
    val quantity: Int,
    val importPrice: Double,
    val imageUrl: String? = null
) {
    val totalPrice: Double get() = quantity * importPrice
}