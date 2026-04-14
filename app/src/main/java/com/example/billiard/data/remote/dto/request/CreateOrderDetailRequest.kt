package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateOrderDetailRequest(
    @SerializedName("invoiceId") val invoiceId: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int = 1,
    @SerializedName("price") val price: Double,
//    @SerializedName("ImageUrl") val imageUrl: String?, // Map chính xác với "ImageUrl" của API
    @SerializedName("note") val note: String? = null
)