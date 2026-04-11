package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateInvoiceRequest(
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("serviceAmount") val serviceAmount: Double,
    @SerializedName("productAmount") val productAmount: Double,
    @SerializedName("taxAmount") val taxAmount: Double,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("voucherId") val voucherId: Int?,
    @SerializedName("employeeId") val employeeId: Int,
    @SerializedName("billiardTableId") val billiardTableId: Int
)