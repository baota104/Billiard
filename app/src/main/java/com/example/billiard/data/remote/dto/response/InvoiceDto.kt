package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class InvoiceDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("serviceAmount") val serviceAmount: Double?,
    @SerializedName("productAmount") val productAmount: Double?,
    @SerializedName("taxAmount") val taxAmount: Double?,
    @SerializedName("totalAmount") val totalAmount: Double?,
    @SerializedName("voucherId") val voucherId: Int?,
    @SerializedName("voucherCode") val voucherCode: String?,
    @SerializedName("employeeId") val employeeId: Int?,
    @SerializedName("employeeName") val employeeName: String?,
    @SerializedName("billiardTableId") val billiardTableId: Int?,
    @SerializedName("billiardTableName") val billiardTableName: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("orderDetails") val details: List<OrderDetailDto>?
)