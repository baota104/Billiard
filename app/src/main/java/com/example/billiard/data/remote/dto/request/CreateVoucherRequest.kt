package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class CreateVoucherRequest(
    @SerializedName("voucherType") val voucherType: String,
    @SerializedName("value") val value: Double,
    @SerializedName("source") val source: String,
    @SerializedName("status") val status: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("minimumAmount") val minimumAmount: Double,
    @SerializedName("maximumValue") val maximumValue: Double?
)