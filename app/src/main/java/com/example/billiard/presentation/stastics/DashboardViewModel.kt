package com.example.billiard.presentation.stastics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.DashboardSummary
import com.example.billiard.domain.model.RevenueChart
import com.example.billiard.domain.usecase.statics.GetDashboardRevenueUseCase
import com.example.billiard.domain.usecase.statics.GetDashboardSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase,
    private val getDashboardRevenueUseCase: GetDashboardRevenueUseCase
) : ViewModel() {

    // 1. State cho Summary (Tổng quan)
    private val _summaryState = MutableStateFlow<Resource<DashboardSummary>>(Resource.Loading)
    val summaryState: StateFlow<Resource<DashboardSummary>> = _summaryState.asStateFlow()

    // 2. State cho Biểu đồ Doanh thu (Chart)
    private val _revenueState = MutableStateFlow<Resource<RevenueChart>>(Resource.Loading)
    val revenueState: StateFlow<Resource<RevenueChart>> = _revenueState.asStateFlow()

    // 3. State lưu trữ mốc thời gian đang chọn (Dùng để UI đổi màu nút: Tuần, Tháng, Năm)
    private val _currentRange = MutableStateFlow("WEEKLY")
    val currentRange: StateFlow<String> = _currentRange.asStateFlow()

    init {
        // Tự động tải dữ liệu ngay khi vào màn hình Thống kê
        loadDashboardSummary()
        loadDashboardRevenue("WEEKLY") // Mặc định hiển thị theo tuần
    }

    // Lấy thông số tổng quan (Doanh thu hôm nay, tỷ lệ tăng trưởng, bàn đang chơi...)
    fun loadDashboardSummary() {
        viewModelScope.launch {
            getDashboardSummaryUseCase().collect { result ->
                _summaryState.value = result
            }
        }
    }

    // Lấy dữ liệu vẽ biểu đồ dựa theo Range (WEEKLY, MONTHLY, YEARLY)
    fun loadDashboardRevenue(range: String) {
        _currentRange.value = range // Lưu lại để UI biết đang ở Tab nào
        viewModelScope.launch {
            getDashboardRevenueUseCase(range).collect { result ->
                _revenueState.value = result
            }
        }
    }
}