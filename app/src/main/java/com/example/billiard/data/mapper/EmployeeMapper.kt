package com.example.billiard.data.mapper

import com.example.billiard.data.remote.dto.request.CreateEmployeeRequest
import com.example.billiard.data.remote.dto.request.UpdateEmployeeRequest
import com.example.billiard.data.remote.dto.response.EmployeeDto
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.request.CreateEmployeeParam
import com.example.billiard.domain.request.UpdateEmployeeParam

// 1. Mapper DTO -> Domain
fun EmployeeDto.toDomain(): Employee {
    return Employee(
        id = this.id,
        firstName = this.firstName.orEmpty(),
        lastName = this.lastName.orEmpty(),
        email = this.email.orEmpty(),
        phoneNumber = this.phoneNumber.orEmpty(),
        role = this.role ?: "UNKNOWN",
        imageUrl = this.imageUrl.orEmpty(),
        isActive = this.isActive ?: false
    )
}

// 2. Mapper Domain -> DTO (Phục vụ cho các hành động POST, PUT)
fun CreateEmployeeParam.toDto(): CreateEmployeeRequest {
    return CreateEmployeeRequest(
        username = this.username,
        password = this.password,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        role = this.role
    )
}

fun UpdateEmployeeParam.toDto(): UpdateEmployeeRequest {
    return UpdateEmployeeRequest(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        role = this.role,
        isActive = this.isActive
    )
}