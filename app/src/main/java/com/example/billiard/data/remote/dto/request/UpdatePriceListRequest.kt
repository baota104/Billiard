package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdatePriceListRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("tableType") val tableType: String
)