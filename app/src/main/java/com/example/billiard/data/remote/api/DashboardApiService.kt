package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.DashboardSummaryDto
import com.example.billiard.data.remote.dto.response.RevenueChartDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApiService {

    @GET("api/v1/dashboard/summary")
    suspend fun getDashboardSummary(): Response<ApiResponse<DashboardSummaryDto>>

    // range truyền vào có thể là: "WEEKLY", "MONTHLY", "YEARLY"
    @GET("api/v1/dashboard/revenue")
    suspend fun getDashboardRevenue(
        @Query("range") range: String
    ): Response<ApiResponse<RevenueChartDto>>
}