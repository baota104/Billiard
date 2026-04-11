package com.example.billiard.domain.usecase.product

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        emit(repository.getAllProducts())
    }
}