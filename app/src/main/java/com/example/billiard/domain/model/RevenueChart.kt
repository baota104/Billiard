package com.example.billiard.domain.model

data class RevenueChart(
    val predict: List<ChartDataPoint>,
    val actual: List<ChartDataPoint>
)

data class ChartDataPoint(
    val dateLabel: String,
    val revenue: Double
)