package com.example.billiard.domain.usecase.statics

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.DashboardSummary
import com.example.billiard.domain.model.RevenueChart
import com.example.billiard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke(): Flow<Resource<DashboardSummary>> {
        return repository.getDashboardSummary()
    }
}
class GetDashboardRevenueUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke(range: String): Flow<Resource<RevenueChart>> {
        return repository.getDashboardRevenue(range)
    }
}