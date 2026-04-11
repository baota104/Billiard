package com.example.billiard.presentation.administrator.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.request.CreateEmployeeParam
import com.example.billiard.domain.request.UpdateEmployeeParam
import com.example.billiard.domain.usecase.employee.CreateEmployeeUseCase
import com.example.billiard.domain.usecase.employee.DeleteEmployeeUseCase
import com.example.billiard.domain.usecase.employee.GetEmployeesUseCase
import com.example.billiard.domain.usecase.employee.UpdateEmployeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeManagementViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val createEmployeeUseCase: CreateEmployeeUseCase,
    private val updateEmployeeUseCase: UpdateEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase
) : ViewModel() {

    private val _employeesState = MutableStateFlow<Resource<PageData<Employee>>?>(null)
    val employeesState: StateFlow<Resource<PageData<Employee>>?> = _employeesState.asStateFlow()

    private val _actionState = MutableStateFlow<Resource<String>?>(null)
    val actionState: StateFlow<Resource<String>?> = _actionState.asStateFlow()

    init {
        loadEmployees(page = 0)
    }

    fun loadEmployees(page: Int = 0, size: Int = 100) {
        viewModelScope.launch {
            getEmployeesUseCase(page, size).collect { result ->
                _employeesState.value = result
            }
        }
    }

    fun createEmployee(param: CreateEmployeeParam) {
        viewModelScope.launch {
            createEmployeeUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    delay(500)
                    loadEmployees()
                }
            }
        }
    }

    fun updateEmployee(param: UpdateEmployeeParam) {
        viewModelScope.launch {
            updateEmployeeUseCase(param).collect { result ->
                when (result) {
                    is Resource.Loading -> _actionState.value = Resource.Loading
                    is Resource.Error -> _actionState.value = Resource.Error(result.message)
                    is Resource.Success -> {
                        _actionState.value = Resource.Success("Cập nhật nhân viên thành công")
                        delay(500)
                        loadEmployees()
                    }
                }
            }
        }
    }

    fun deleteEmployee(employeeId: Int) {
        viewModelScope.launch {
            deleteEmployeeUseCase(employeeId).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    delay(500)
                    loadEmployees()
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}