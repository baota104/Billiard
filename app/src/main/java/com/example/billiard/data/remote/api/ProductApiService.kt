package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.PageDto
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

    // Sửa API GET: Yêu cầu phân trang và trả về PageDto thay vì List
    @GET("api/v1/products")
    suspend fun getAllProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String> = listOf("id,asc")
    ): Response<ApiResponse<PageDto<ProductDto>>>

    @GET("api/v1/products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String
    ): Response<ApiResponse<List<ProductDto>>>

    @GET("api/v1/products/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: Int
    ): Response<ApiResponse<ProductDto>>

    // Giữ nguyên upsert vì đã là Multipart
    @Multipart
    @POST("api/v1/products/upsert")
    suspend fun upsertProduct(
        // API Swagger yêu cầu gửi lên các trường riêng rẽ
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part("sellingPrice") sellingPrice: RequestBody,
        @Part("importPrice") importPrice: RequestBody,
        @Part("initStock") initStock: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponse<ProductDto>>

    @DELETE("api/v1/products")
    suspend fun deleteProduct(
        @Query("productId") productId: Int
    ): Response<ApiResponse<String>>
}