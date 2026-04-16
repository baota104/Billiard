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

    private val _summaryState = MutableStateFlow<Resource<DashboardSummary>>(Resource.Loading)
    val summaryState: StateFlow<Resource<DashboardSummary>> = _summaryState.asStateFlow()

    private val _revenueState = MutableStateFlow<Resource<RevenueChart>>(Resource.Loading)
    val revenueState: StateFlow<Resource<RevenueChart>> = _revenueState.asStateFlow()

    private val _currentRange = MutableStateFlow("WEEKLY")
    val currentRange: StateFlow<String> = _currentRange.asStateFlow()

    init {
        loadDashboardSummary()
        loadDashboardRevenue("WEEKLY")
    }

    fun loadDashboardSummary() {
        viewModelScope.launch {
            getDashboardSummaryUseCase().collect { result ->
                _summaryState.value = result
            }
        }
    }

    fun loadDashboardRevenue(range: String) {
        _currentRange.value = range
        viewModelScope.launch {
            getDashboardRevenueUseCase(range).collect { result ->
                _revenueState.value = result
            }
        }
    }
}