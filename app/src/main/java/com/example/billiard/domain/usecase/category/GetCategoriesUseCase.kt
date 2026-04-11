package com.example.billiard.domain.usecase.category

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading)
        emit(repository.getCategories())
    }
}