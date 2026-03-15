package com.example.billiard.data.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.TokenManager
import com.example.billiard.data.remote.api.AuthApiService
import com.example.billiard.data.remote.dto.LoginRequest
import com.example.billiard.data.remote.dto.LoginResponse
import com.example.billiard.domain.repository.IAuthRepository

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : IAuthRepository {

    override suspend fun login(request: LoginRequest): Resource<LoginResponse> {
        return try {
            val response = apiService.login(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                tokenManager.saveToken(body.token)
                Resource.Success(body)
            } else {
                Resource.Error(response.message() ?: "Tài khoản hoặc mật khẩu không chính xác")
            }
        } catch (e: Exception) {
            // Xử lý mất mạng hoặc server chết
            Resource.Error("Lỗi kết nối máy chủ: ${e.localizedMessage}")
        }
    }
}