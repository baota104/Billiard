package com.example.billiard.data.repository

import android.util.Log
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.SessionManager
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.remote.api.AuthApiService
import com.example.billiard.data.remote.dto.LoginRequest
import com.example.billiard.domain.model.UserAuth
import com.example.billiard.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Resource<UserAuth> {
        return try {
            val response = api.login(LoginRequest(username, password))

            if (response.isSuccessful && response.body()?.status == 200) {
                val authBody = response.body()!!.body!!

                sessionManager.saveAuthData(
                    accessToken = authBody.accessToken,
                    refreshToken = authBody.refreshToken,
                    role = authBody.employee.role
                )
                Resource.Success(authBody.toDomain())
            } else {
                // ĐỌC LỖI TỪ errorBody()
                val errorString = response.errorBody()?.string()
                Log.e("AuthRepositoryImpl", "Login failed (Code ${response.code()}): $errorString")

                // Trả lỗi về cho UI
                Resource.Error("Sai tài khoản hoặc mật khẩu")
            }
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Login error: ${e.message}", e)
            Resource.Error("Lỗi kết nối: Không thể gọi tới Server!", e)
        }
    }
}