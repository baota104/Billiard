package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.ProductDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {

    @GET("api/v1/products")
    suspend fun getAllProducts(): Response<ApiResponse<List<ProductDto>>>

    @GET("api/v1/products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String
    ): Response<ApiResponse<List<ProductDto>>>

    @GET("api/v1/products/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: Int
    ): Response<ApiResponse<ProductDto>>

    // Dùng @Multipart để gửi kèm file ảnh và JSON object
    @Multipart
    @POST("api/v1/products/upsert")
    suspend fun upsertProduct(
        @Part("product") product: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponse<ProductDto>>

    @DELETE("api/v1/products")
    suspend fun deleteProduct(
        @Query("productId") productId: Int
    ): Response<ApiResponse<String>>
}