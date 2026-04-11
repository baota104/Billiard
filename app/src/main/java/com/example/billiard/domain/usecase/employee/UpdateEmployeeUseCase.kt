package com.example.billiard.domain.usecase.employee

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.request.UpdateEmployeeParam
import com.example.billiard.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateEmployeeUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    operator fun invoke(param: UpdateEmployeeParam): Flow<Resource<Employee>> = flow {
        emit(Resource.Loading)
        emit(repository.updateEmployee(param))
    }
}