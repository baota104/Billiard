package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.data.remote.dto.LoginRequest
import com.example.billiard.data.remote.dto.LoginResponse

interface IAuthRepository {
    suspend fun login(request: LoginRequest): Resource<LoginResponse>
}