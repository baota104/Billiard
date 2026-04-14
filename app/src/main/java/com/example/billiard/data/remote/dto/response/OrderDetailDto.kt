package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class OrderDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("invoiceId") val invoiceId: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("productName") val productName: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("note") val note: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("categoryType") val categoryType: String?,
    @SerializedName("productImageUrl") val imageUrl: String?,
    @SerializedName("deleted") val deleted: Boolean
)