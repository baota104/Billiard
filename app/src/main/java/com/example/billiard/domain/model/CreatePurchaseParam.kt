package com.example.billiard.domain.model

data class CreatePurchaseParam(
    val details: List<PurchaseDetailParam>
)

data class PurchaseDetailParam(
    val productId: Int,
    val quantity: Int,
    val importPrice: Double
)