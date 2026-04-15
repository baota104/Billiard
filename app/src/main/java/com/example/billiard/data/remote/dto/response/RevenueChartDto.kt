package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class RevenueChartDto(
    @SerializedName("predict") val predict: List<ChartDataPointDto>?,
    @SerializedName("actual") val actual: List<ChartDataPointDto>?
)

data class ChartDataPointDto(
    @SerializedName("dateLabel") val dateLabel: String?,
    @SerializedName("revenue") val revenue: Double?
)