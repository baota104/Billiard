package com.example.billiard.presentation.home.homscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateInvoiceParam
import com.example.billiard.domain.model.DashboardTable
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.usecase.invoice.CreateInvoiceUseCase
import com.example.billiard.domain.usecase.table.GetDashboardTablesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    private val getDashboardTablesUseCase: GetDashboardTablesUseCase,
    private val createInvoiceUseCase: CreateInvoiceUseCase 
) : ViewModel() {

    private val _tablesState = MutableStateFlow<Resource<List<DashboardTable>>>(Resource.Loading)
    val tablesState: StateFlow<Resource<List<DashboardTable>>> = _tablesState.asStateFlow()

    private val _openTableState = MutableStateFlow<Resource<Invoice>?>(null)
    val openTableState: StateFlow<Resource<Invoice>?> = _openTableState.asStateFlow()

    private var currentPage = 0
    private var isLastPage = false
    var isLoadingMore = false 

    private val currentTableList = mutableListOf<DashboardTable>()

    init {
        loadTables()
    }

    fun loadTables(isRefresh: Boolean = false) {
        if (isLoadingMore || (isLastPage && !isRefresh)) return

        if (isRefresh) {
            currentPage = 0
            isLastPage = false
            currentTableList.clear()
            _tablesState.value = Resource.Loading
            Log.d("TableViewModel_DEBUG", "Bắt đầu gọi API lấy danh sách bàn (Refresh)...")
        }

        isLoadingMore = true

        viewModelScope.launch {
            getDashboardTablesUseCase(page = currentPage, size = 10).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        currentTableList.addAll(result.data.content)
                        isLastPage = result.data.isLastPage
                        if (!isLastPage) currentPage++

                        Log.d("TableViewModel_DEBUG", "Lấy danh sách bàn THÀNH CÔNG. Số lượng: ${currentTableList.size}")
                        
                        _tablesState.value = Resource.Success(currentTableList.toList())
                        isLoadingMore = false
                    }
                    is Resource.Error -> {
                        Log.e("TableViewModel_DEBUG", "Lấy danh sách bàn LỖI: ${result.message}")
                        _tablesState.value = Resource.Error(result.message)
                        isLoadingMore = false
                    }
                    is Resource.Loading -> { }
                }
            }
        }
    }

    fun openTable(employeeId: Int, tableId: Int) {
        Log.d("TableViewModel_DEBUG", "Gửi request MỞ BÀN: employeeId=$employeeId, tableId=$tableId")
        viewModelScope.launch {
            val param = CreateInvoiceParam(
                employeeId = employeeId,
                billiardTableId = tableId,
                startTime = Date() 
            )

            createInvoiceUseCase(param).collect { result ->
                _openTableState.value = result
                
                when (result) {
                    is Resource.Success -> {
                        Log.d("TableViewModel_DEBUG", "Mở bàn THÀNH CÔNG! Đợi 2000ms rồi reload lại danh sách...")
                        // Nếu Backend xử lý logic quá chậm, tạm thời tăng thời gian chờ lên 2 giây
                        delay(2000)
                        loadTables(isRefresh = true)
                    }
                    is Resource.Error -> {
                        Log.e("TableViewModel_DEBUG", "Mở bàn LỖI: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d("TableViewModel_DEBUG", "Đang mở bàn...")
                    }
                }
            }
        }
    }

    fun resetOpenTableState() {
        _openTableState.value = null
    }
}