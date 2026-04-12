package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PurchaseInvoiceDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("totalAmount") val totalAmount: Double?,
    @SerializedName("importDate") val importDate: String?,
    @SerializedName("employeeId") val employeeId: Int?,
    @SerializedName("employeeName") val employeeName: String?,
    @SerializedName("details") val details: List<PurchaseDetailDto>?
)

data class PurchaseDetailDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("productId") val productId: Int?,
    @SerializedName("productName") val productName: String?,
    @SerializedName("quantity") val quantity: Int?,
    @SerializedName("importPrice") val importPrice: Double?,
    @SerializedName("subTotal") val subTotal: Double?
)

data class PurchaseHistoryDto(
    @SerializedName("purchaseId") val purchaseId: Int?,
    @SerializedName("totalPrice") val totalPrice: Double?,
    @SerializedName("purchaseDate") val purchaseDate: String?,
    @SerializedName("employeeName") val employeeName: String?
)