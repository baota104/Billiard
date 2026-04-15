package com.example.billiard.presentation.stastics

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.R
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.DashboardSummary
import com.example.billiard.domain.model.RevenueChart
import com.example.billiard.domain.usecase.statics.GetDashboardRevenueUseCase
import com.example.billiard.domain.usecase.statics.GetDashboardSummaryUseCase
import com.example.billiard.presentation.stastics.charts.RevenueChartManager
import com.github.mikephil.charting.charts.LineChart
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

    // Chart Manager instance (để quản lý biểu đồ)
    private var chartManager: RevenueChartManager? = null

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

    // ============ Chart Management Methods (từ StasticsFragment) ============

    /**
     * Khởi tạo Chart Manager
     */
    fun initializeChart(context: Context) {
        chartManager = RevenueChartManager(context)
    }

    /**
     * Setup biểu đồ và tải dữ liệu mặc định
     */
    fun setupChart(view: View) {
        val chart = view.findViewById<LineChart>(R.id.revenueChart)

        if (chartManager == null) {
            throw IllegalStateException("ChartManager not initialized. Call initializeChart() first")
        }

        // Setup chart
        chartManager?.setupChart(chart)

        // Load dữ liệu tuần mặc định
        chartManager?.loadWeekData(chart)

        // Setup bộ lọc (filter buttons)
        chartManager?.setupFilter(view, chart)
    }

    /**
     * Thay thế dữ liệu biểu đồ theo loại Filter
     */
    fun loadChartData(view: View, filterType: String) {
        val chart = view.findViewById<LineChart>(R.id.revenueChart)

        chartManager?.let {
            chart.clear()
            when (filterType) {
                "WEEK" -> it.loadWeekData(chart)
                "MONTH" -> it.loadMonthData(chart)
                "YEAR" -> it.loadYearData(chart)
                "ALL" -> it.loadAllData(chart)
            }
        }
    }
}