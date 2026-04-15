package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.DashboardSummary
import com.example.billiard.domain.model.RevenueChart
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardSummary(): Flow<Resource<DashboardSummary>>
    fun getDashboardRevenue(range: String): Flow<Resource<RevenueChart>>
}