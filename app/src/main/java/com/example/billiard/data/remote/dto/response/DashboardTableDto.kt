package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class DashboardTableDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("tableType") val tableType: String?,
    @SerializedName("imageUrl") val imageUrl: String?, // Cần nhận thêm ảnh từ Backend
    @SerializedName("activeInvoice") val activeInvoice: ActiveInvoiceDto?
)

data class ActiveInvoiceDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("startAt") val startAt: String?,
    @SerializedName("employeeName") val employeeName: String?
)