package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.*
import com.example.billiard.data.remote.dto.response.ChartDataPointDto
import com.example.billiard.data.remote.dto.response.DashboardSummaryDto
import com.example.billiard.data.remote.dto.response.GrowthSummaryDto
import com.example.billiard.data.remote.dto.response.PlaytimeSummaryDto
import com.example.billiard.data.remote.dto.response.RevenueChartDto
import com.example.billiard.data.remote.dto.response.RevenueSummaryDto
import com.example.billiard.data.remote.dto.response.TableSummaryDto
import com.example.billiard.domain.model.*

fun DashboardSummaryDto.toDomain(): DashboardSummary {
    return DashboardSummary(
        revenueSummary = this.revenueSummary?.toDomain() ?: RevenueSummary(0.0, 0.0, 0.0),
        playtimeSummary = this.playtimeSummary?.toDomain() ?: PlaytimeSummary(0.0, 0.0, 0.0),
        growthSummary = this.growthSummary?.toDomain() ?: GrowthSummary(0.0),
        tableSummary = this.tableSummary?.toDomain() ?: TableSummary(0, 0, 0.0)
    )
}

fun RevenueSummaryDto.toDomain() = RevenueSummary(
    todayRevenue = this.todayRevenue ?: 0.0,
    lastWeekTodayRevenue = this.lastWeekTodayRevenue ?: 0.0,
    changePercentage = this.changePercentage ?: 0.0
)

fun PlaytimeSummaryDto.toDomain() = PlaytimeSummary(
    avgPlaytime = this.avgPlaytime ?: 0.0,
    lastWeekAvgPlaytime = this.lastWeekAvgPlaytime ?: 0.0,
    changePercentage = this.changePercentage ?: 0.0
)

fun GrowthSummaryDto.toDomain() = GrowthSummary(
    growthRate = this.growthRate ?: 0.0
)

fun TableSummaryDto.toDomain() = TableSummary(
    activeTables = this.activeTables ?: 0,
    totalTables = this.totalTables ?: 0,
    utilizationRate = this.utilizationRate ?: 0.0
)

// --- Mapper cho Chart ---

fun RevenueChartDto.toDomain(): RevenueChart {
    return RevenueChart(
        predict = this.predict?.map { it.toDomain() } ?: emptyList(),
        actual = this.actual?.map { it.toDomain() } ?: emptyList()
    )
}

fun ChartDataPointDto.toDomain() = ChartDataPoint(
    dateLabel = this.dateLabel.orEmpty(),
    revenue = this.revenue ?: 0.0
)