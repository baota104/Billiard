package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateInvoiceRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("startTime") val startTime: String?, // Backend là LocalDateTime, Android chuyển thành String ISO
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("status") val status: String,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("serviceAmount") val serviceAmount: Double,
    @SerializedName("productAmount") val productAmount: Double,
    @SerializedName("taxAmount") val taxAmount: Double,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("voucherId") val voucherId: Int?,
    @SerializedName("employeeId") val employeeId: Int,
    @SerializedName("billiardTableId") val billiardTableId: Int
)