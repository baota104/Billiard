package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class VoucherDto(
    @SerializedName("id") val id: Long?, // Bổ sung ID để xử lý chức năng sửa/xóa
    @SerializedName("code") val code: String?,
    @SerializedName("voucherType") val voucherType: String?,
    @SerializedName("value") val value: Double?,
    @SerializedName("source") val source: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("quantity") val quantity: Int?,
    @SerializedName("minimumAmount") val minimumAmount: Double?
)