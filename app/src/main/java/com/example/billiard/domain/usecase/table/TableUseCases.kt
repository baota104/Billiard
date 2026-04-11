package com.example.billiard.domain.usecase.table

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateTableParam
import com.example.billiard.domain.model.DashboardTable
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.model.UpdateTableParam
import com.example.billiard.domain.repository.TableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDashboardTablesUseCase @Inject constructor(private val repository: TableRepository) {
    operator fun invoke(page: Int = 0, size: Int = 20): Flow<Resource<PageData<DashboardTable>>> = flow {
        emit(Resource.Loading)
        emit(repository.getDashboardTables(page, size))
    }
}

class CreateTableUseCase @Inject constructor(private val repository: TableRepository) {
    operator fun invoke(param: CreateTableParam): Flow<Resource<DashboardTable>> = flow {
        emit(Resource.Loading)
        emit(repository.createTable(param))
    }
}

class UpdateTableUseCase @Inject constructor(private val repository: TableRepository) {
    operator fun invoke(param: UpdateTableParam): Flow<Resource<DashboardTable>> = flow {
        emit(Resource.Loading)
        emit(repository.updateTable(param))
    }
}

class DeleteTableUseCase @Inject constructor(private val repository: TableRepository) {
    operator fun invoke(id: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        emit(repository.deleteTable(id))
    }
}