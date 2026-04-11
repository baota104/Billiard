package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam

interface ProductRepository {
    suspend fun getAllProducts(): Resource<List<Product>>
    suspend fun searchProducts(keyword: String): Resource<List<Product>>
    suspend fun getProductById(productId: Int): Resource<Product>
    suspend fun upsertProduct(param: UpsertProductParam): Resource<Product>
    suspend fun deleteProduct(productId: Int): Resource<String>
}