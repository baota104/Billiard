package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.request.CreateBankRequest
import com.example.billiard.data.remote.dto.request.UpdateBankRequest
import com.example.billiard.data.remote.dto.response.ApiResponse
import com.example.billiard.data.remote.dto.response.BankDto
import com.example.billiard.data.remote.dto.response.VietQrDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BankApiService {

    @GET("api/v1/banks")
    suspend fun getBanks(): Response<ApiResponse<List<BankDto>>>

    @Headers("Content-Type: application/json")
    @POST("api/v1/banks")
    suspend fun createBank(
        @Body request: CreateBankRequest
    ): Response<ApiResponse<BankDto>>

    @Headers("Content-Type: application/json")
    @PUT("api/v1/banks")
    suspend fun updateBank(
        @Body request: UpdateBankRequest
    ): Response<ApiResponse<BankDto>>

    @DELETE("api/v1/banks/{bankId}")
    suspend fun deleteBank(
        @Path("bankId") bankId: Int
    ): Response<ApiResponse<String>>

    @GET("api/v1/banks/vietqr/{id}")
    suspend fun getVietQr(
        @Path("id") id: Int
    ): Response<ApiResponse<VietQrDto>>
}