package com.example.billiard.domain.usecase.employee

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteEmployeeUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    operator fun invoke(employeeId: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        emit(repository.deleteEmployee(employeeId))
    }
}