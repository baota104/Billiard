package com.example.billiard.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateVoucherRequest(
    @SerializedName("status") val status: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("quantity") val quantity: Int
)