package com.example.billiard.domain.model

data class DashboardSummary(
    val revenueSummary: RevenueSummary,
    val playtimeSummary: PlaytimeSummary,
    val growthSummary: GrowthSummary,
    val tableSummary: TableSummary
)

data class RevenueSummary(
    val todayRevenue: Double,
    val lastWeekTodayRevenue: Double,
    val changePercentage: Double
)

data class PlaytimeSummary(
    val avgPlaytime: Double, // Tính theo giờ hoặc phút tùy logic BE
    val lastWeekAvgPlaytime: Double,
    val changePercentage: Double
)

data class GrowthSummary(
    val growthRate: Double
)

data class TableSummary(
    val activeTables: Int,
    val totalTables: Int,
    val utilizationRate: Double
)