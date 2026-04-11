package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreateCategoryRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.CategoryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CategoryApiService {
    @GET("api/v1/categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    @POST("api/v1/categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): Response<ApiResponse<CategoryDto>>
}