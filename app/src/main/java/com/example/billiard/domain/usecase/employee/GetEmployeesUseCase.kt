package com.example.billiard.domain.usecase.employee

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    operator fun invoke(page: Int = 0, size: Int = 20): Flow<Resource<PageData<Employee>>> = flow {
        emit(Resource.Loading)
        val result = repository.getEmployees(page, size)
        emit(result)
    }
}