package com.example.billiard.presentation.administrator.table

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateTableParam
import com.example.billiard.domain.model.DashboardTable
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.model.UpdateTableParam
import com.example.billiard.domain.usecase.table.CreateTableUseCase
import com.example.billiard.domain.usecase.table.DeleteTableUseCase
import com.example.billiard.domain.usecase.table.GetDashboardTablesUseCase
import com.example.billiard.domain.usecase.table.UpdateTableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableManageViewModel @Inject constructor(
    private val getDashboardTablesUseCase: GetDashboardTablesUseCase,
    private val createTableUseCase: CreateTableUseCase,
    private val updateTableUseCase: UpdateTableUseCase,
    private val deleteTableUseCase: DeleteTableUseCase
) : ViewModel() {

    private val _dashboardTablesState = MutableStateFlow<Resource<PageData<DashboardTable>>?>(null)
    val dashboardTablesState: StateFlow<Resource<PageData<DashboardTable>>?> = _dashboardTablesState.asStateFlow()

    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    init {
        loadDashboardTables()
    }

    fun loadDashboardTables(page: Int = 0, size: Int = 100) {
        viewModelScope.launch {
            getDashboardTablesUseCase(page, size).collect { result ->
                _dashboardTablesState.value = result
            }
        }
    }

    fun createTable(param: CreateTableParam) {
        Log.d("TableManage_DEBUG", "GỌI API TẠO BÀN: name='${param.name}', status='${param.status}'")
        viewModelScope.launch {
            createTableUseCase(param).collect { result ->
                _actionState.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("TableManage_DEBUG", "Tạo bàn THÀNH CÔNG! ID trả về: ${result.data.id}")
                        delay(500) // Đợi 1 chút để DB của Backend kịp commit
                        loadDashboardTables()
                    }
                    is Resource.Error -> {
                        Log.e("TableManage_DEBUG", "LỖI KẾT NỐI TẠO BÀN: ${result.message} - Exception: ${result.exception?.message}")
                    }
                    is Resource.Loading -> { }
                }
            }
        }
    }

    fun updateTable(param: UpdateTableParam) {
        Log.d("TableManage_DEBUG", "GỌI API CẬP NHẬT BÀN ID=${param.id}: name='${param.name}', status='${param.status}'")
        viewModelScope.launch {
            updateTableUseCase(param).collect { result ->
                _actionState.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("TableManage_DEBUG", "Cập nhật bàn THÀNH CÔNG! ID: ${result.data.id}")
                        delay(500)
                        loadDashboardTables()
                    }
                    is Resource.Error -> {
                        Log.e("TableManage_DEBUG", "LỖI KẾT NỐI CẬP NHẬT BÀN: ${result.message} - Exception: ${result.exception?.message}")
                    }
                    is Resource.Loading -> { }
                }
            }
        }
    }

    fun deleteTable(id: Int) {
        Log.d("TableManage_DEBUG", "GỌI API XÓA BÀN ID=$id")
        viewModelScope.launch {
            deleteTableUseCase(id).collect { result ->
                _actionState.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("TableManage_DEBUG", "Xóa bàn THÀNH CÔNG!")
                        delay(500) // Khắc phục lỗi xóa xong list không update kịp
                        loadDashboardTables()
                    }
                    is Resource.Error -> {
                        Log.e("TableManage_DEBUG", "LỖI KẾT NỐI XÓA BÀN: ${result.message} - Exception: ${result.exception?.message}")
                    }
                    is Resource.Loading -> { }
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}