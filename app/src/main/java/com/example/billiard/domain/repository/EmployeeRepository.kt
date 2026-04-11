package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.request.CreateEmployeeParam
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.request.UpdateEmployeeParam

interface EmployeeRepository {
    suspend fun getEmployees(page: Int, size: Int): Resource<PageData<Employee>>
    
    // Đã thay thế DTO (Data) bằng Param (Domain)
    suspend fun createEmployee(param: CreateEmployeeParam): Resource<String>
    
    suspend fun updateEmployee(param: UpdateEmployeeParam): Resource<Employee>
    
    suspend fun deleteEmployee(employeeId: Int): Resource<String>
}