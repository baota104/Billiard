package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.EmployeeApiService
import com.example.billiard.domain.request.CreateEmployeeParam
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.request.UpdateEmployeeParam
import com.example.billiard.domain.repository.EmployeeRepository
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val api: EmployeeApiService
) : BaseRepository(), EmployeeRepository {

    override suspend fun getEmployees(page: Int, size: Int): Resource<PageData<Employee>> {
        return safeApiCall(
            apiCall = { api.getEmployees(page, size) },
            mapper = { pageDto -> 
                pageDto.toDomain { it.toDomain() } 
            }
        )
    }

    override suspend fun createEmployee(param: CreateEmployeeParam): Resource<String> {
        return safeApiCall(
            apiCall = { api.createEmployee(param.toDto()) },
            mapper = { "Thêm nhân viên thành công" }
        )
    }

    override suspend fun updateEmployee(param: UpdateEmployeeParam): Resource<Employee> {
        return safeApiCall(
            apiCall = { api.updateEmployee(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deleteEmployee(employeeId: Int): Resource<String> {
        return safeApiCall(
            apiCall = { api.deleteEmployee(employeeId) },
            mapper = { "Xóa nhân viên thành công" }
        )
    }
}