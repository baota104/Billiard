package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreatePurchaseInvoiceRequest(
    @SerializedName("details") val details: List<PurchaseDetailRequest>
)

data class PurchaseDetailRequest(
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("importPrice") val importPrice: Double
)