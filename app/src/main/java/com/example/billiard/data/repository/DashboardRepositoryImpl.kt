package com.example.billiard.data.repository

import android.util.Log
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.remote.api.DashboardApiService
import com.example.billiard.domain.model.DashboardSummary
import com.example.billiard.domain.model.RevenueChart
import com.example.billiard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val api: DashboardApiService
) : DashboardRepository {

    override fun getDashboardSummary(): Flow<Resource<DashboardSummary>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.getDashboardSummary()
            if (response.isSuccessful) {
                val body = response.body()
                // Backend của bạn dùng status = 0 là thành công
                if (body != null && (body.status == 200 || body.status == 0)) {
                    val data = body.body
                    if (data != null) {
                        emit(Resource.Success(data.toDomain()))
                    } else {
                        emit(Resource.Error("Dữ liệu Summary trống"))
                    }
                } else {
                    emit(Resource.Error(body?.message ?: "Lỗi từ Server"))
                }
            } else {
                emit(Resource.Error("Lỗi kết nối: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("DashboardRepo", "Lỗi Summary: ${e.message}", e)
            emit(Resource.Error("Không thể tải dữ liệu thống kê", e))
        }
    }

    override fun getDashboardRevenue(range: String): Flow<Resource<RevenueChart>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.getDashboardRevenue(range)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && (body.status == 200 || body.status == 0)) {
                    val data = body.body
                    if (data != null) {
                        emit(Resource.Success(data.toDomain()))
                    } else {
                        emit(Resource.Error("Dữ liệu Biểu đồ trống"))
                    }
                } else {
                    emit(Resource.Error(body?.message ?: "Lỗi từ Server"))
                }
            } else {
                emit(Resource.Error("Lỗi kết nối: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("DashboardRepo", "Lỗi Revenue Chart: ${e.message}", e)
            emit(Resource.Error("Không thể tải dữ liệu biểu đồ", e))
        }
    }
}