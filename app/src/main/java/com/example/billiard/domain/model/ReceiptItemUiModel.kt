package com.example.billiard.domain.model

data class ReceiptItemUiModel(
    val id: String,
    val name: String,
    val quantity: Int,
    val importPrice: Int,
    val imageUrl: String? = null
) {
    val totalPrice: Int get() = quantity * importPrice
}