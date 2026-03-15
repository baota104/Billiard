package com.example.billiard.data.remote.api

import com.example.billiard.data.remote.dto.LoginRequest
import com.example.billiard.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login") // Đảm bảo đường dẫn này khớp với backend của bạn
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}