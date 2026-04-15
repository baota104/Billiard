package com.example.billiard.data.remote.dto.response

import com.google.gson.annotations.SerializedName


data class DashboardSummaryDto(
    @SerializedName("revenueSummary") val revenueSummary: RevenueSummaryDto?,
    @SerializedName("playtimeSummary") val playtimeSummary: PlaytimeSummaryDto?,
    @SerializedName("growthSummary") val growthSummary: GrowthSummaryDto?,
    @SerializedName("tableSummary") val tableSummary: TableSummaryDto?
)

data class RevenueSummaryDto(
    @SerializedName("todayRevenue") val todayRevenue: Double?,
    @SerializedName("lastWeekTodayRevenue") val lastWeekTodayRevenue: Double?,
    @SerializedName("changePercentage") val changePercentage: Double?
)

data class PlaytimeSummaryDto(
    @SerializedName("avgPlaytime") val avgPlaytime: Double?,
    @SerializedName("lastWeekAvgPlaytime") val lastWeekAvgPlaytime: Double?,
    @SerializedName("changePercentage") val changePercentage: Double?
)

data class GrowthSummaryDto(
    @SerializedName("growthRate") val growthRate: Double?
)

data class TableSummaryDto(
    @SerializedName("activeTables") val activeTables: Int?,
    @SerializedName("totalTables") val totalTables: Int?,
    @SerializedName("utilizationRate") val utilizationRate: Double?
)