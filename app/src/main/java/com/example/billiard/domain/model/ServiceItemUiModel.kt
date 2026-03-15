package com.example.billiard.domain.model

data class ServiceItemUiModel(
    val id: String,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val categoryId: String,
    val isRental: Boolean = false
) {
    val formattedPrice: String
        get() = "%,dđ".format(price).replace(',', '.')
}