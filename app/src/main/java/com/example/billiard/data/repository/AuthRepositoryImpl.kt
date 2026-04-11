package com.example.billiard.data.repository

import com.example.billiard.core.base.BaseRepository
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.SessionManager
import com.example.billiard.data.mapper.toDomain
import com.example.billiard.data.remote.api.AuthApiService
import com.example.billiard.data.remote.dto.request.LoginRequest
import com.example.billiard.domain.model.UserAuth
import com.example.billiard.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) : BaseRepository(), AuthRepository {

    override suspend fun login(username: String, password: String): Resource<UserAuth> {
        return safeApiCall(
            apiCall = { api.login(LoginRequest(username, password)) },
            mapper = { authBody -> authBody.toDomain() },
            onSuccess = { authBody ->
                // Lưu token ngay khi đăng nhập thành công
                sessionManager.saveAuthData(
                    accessToken = authBody.accessToken,
                    refreshToken = authBody.refreshToken,
                    role = authBody.employee.role,
                    id = authBody.employee.id
                )
            }
        )
    }
}