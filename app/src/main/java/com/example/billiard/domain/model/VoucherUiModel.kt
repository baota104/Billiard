package com.example.billiard.domain.model

data class VoucherUiModel(
    val id: String,
    val code: String,
    val discountText: String,
    val minOrderText: String,
    val expiryText: String,
    val isAiRecommended: Boolean = false
)