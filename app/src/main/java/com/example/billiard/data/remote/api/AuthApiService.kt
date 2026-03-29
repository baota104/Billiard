package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.ApiResponse
import com.example.billiard.data.remote.dto.AuthBody
import com.example.billiard.data.remote.dto.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthBody>>
}