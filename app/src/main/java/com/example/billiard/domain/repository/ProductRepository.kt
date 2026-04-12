package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam

interface ProductRepository {
    // Sửa List<Product> thành PageData<Product>
    suspend fun getAllProducts(page: Int, size: Int): Resource<PageData<Product>>

    suspend fun searchProducts(keyword: String): Resource<List<Product>>
    suspend fun getProductById(productId: Int): Resource<Product>
    suspend fun upsertProduct(param: UpsertProductParam): Resource<Product>
    suspend fun deleteProduct(productId: Int): Resource<String>
}