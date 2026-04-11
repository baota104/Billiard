package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.request.CreateCategoryParam

interface CategoryRepository {
    suspend fun getCategories(): Resource<List<Category>>
    suspend fun createCategory(param: CreateCategoryParam): Resource<Category>
}