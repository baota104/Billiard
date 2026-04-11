package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.mapper.toDto
import com.example.billiard.data.remote.api.CategoryApiService
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.request.CreateCategoryParam
import com.example.billiard.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApiService
) : BaseRepository(), CategoryRepository {

    override suspend fun getCategories(): Resource<List<Category>> {
        return safeApiCall(
            apiCall = { api.getCategories() },
            mapper = { listDto -> listDto.map { it.toDomain() } }
        )
    }

    override suspend fun createCategory(param: CreateCategoryParam): Resource<Category> {
        return safeApiCall(
            apiCall = { api.createCategory(param.toDto()) },
            mapper = { it.toDomain() }
        )
    }
}