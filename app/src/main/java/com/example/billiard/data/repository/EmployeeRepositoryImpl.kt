package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.EmployeeApiService
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.repository.EmployeeRepository
import com.example.billiard.domain.request.CreateEmployeeParam
import com.example.billiard.domain.request.UpdateEmployeeParam
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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
        // Backend đang nhận Multipart Form Data nên phải đóng gói lại dưới dạng RequestBody
        val idBody = param.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        
        // Tránh lỗi ném chuỗi rỗng ("") lên Backend gây HTTP 415 hoặc null exception
        val firstNameBody = param.firstName.ifBlank { "N/A" }.toRequestBody("text/plain".toMediaTypeOrNull())
        val lastNameBody = param.lastName.ifBlank { "N/A" }.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = param.email.ifBlank { "noemail@billiard.com" }.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneBody = param.phoneNumber.ifBlank { "0000000000" }.toRequestBody("text/plain".toMediaTypeOrNull())

        val roleBody = param.role.toRequestBody("text/plain".toMediaTypeOrNull())
        val isActiveBody = param.isActive.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return safeApiCall(
            apiCall = { 
                api.updateEmployee(idBody, firstNameBody, lastNameBody, emailBody, phoneBody, roleBody, isActiveBody) 
            },
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