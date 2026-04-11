package com.example.billiard.domain.usecase.product

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam
import com.example.billiard.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpsertProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(param: UpsertProductParam): Flow<Resource<Product>> = flow {
        emit(Resource.Loading)
        emit(repository.upsertProduct(param))
    }
}