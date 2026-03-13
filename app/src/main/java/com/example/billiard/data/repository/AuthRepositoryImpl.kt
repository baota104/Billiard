package com.example.billiard.data.repository

import com.example.billiard.data.remote.auth.AuthApiService
import com.example.billiard.data.remote.dto.LoginResponse

class AuthRepositoryImpl(
    private val api: AuthApiService
) : IAuthRepository {

    override suspend fun login(
        username: String,
        password: String
    ): LoginResponse {

        return api.login(
            clientId = "mobile-client",
            grantType = "password",
            username = username,
            password = password
        )
    }
}