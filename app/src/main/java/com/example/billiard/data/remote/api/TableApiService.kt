package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.DashboardTableDto
import com.example.billiard.data.remote.dto.response.PageDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TableApiService {
    @GET("api/v1/tables")
    suspend fun getTables(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<ApiResponse<PageDto<DashboardTableDto>>> // Dùng chung DashboardTableDto

    @GET("api/v1/tables/{tableId}")
    suspend fun getTableById(
        @Path("tableId") tableId: Int
    ): Response<ApiResponse<DashboardTableDto>>

    @Multipart
    @POST("api/v1/tables")
    suspend fun createTable(
        @Part("name") name: RequestBody,
        @Part("status") status: RequestBody,
        @Part("tableType") tableType: RequestBody,
        @Part imageFile: MultipartBody.Part?
    ): Response<ApiResponse<DashboardTableDto>>

    @Multipart
    @PUT("api/v1/tables")
    suspend fun updateTable(
        @Query("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("status") status: RequestBody,
        @Part("tableType") tableType: RequestBody,
        @Part imageFile: MultipartBody.Part?
    ): Response<ApiResponse<DashboardTableDto>>

    @DELETE("api/v1/tables/{tableId}")
    suspend fun deleteTable(
        @Path("tableId") id: Int
    ): Response<ApiResponse<Any>>

    @GET("api/v1/tables/dashboard")
    suspend fun getDashboardTables(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String> = listOf("id,asc")
    ): Response<ApiResponse<PageDto<DashboardTableDto>>>
}