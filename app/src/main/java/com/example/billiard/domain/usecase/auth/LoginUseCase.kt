package com.example.billiard.domain.usecase.auth

import com.example.billiard.core.network.Resource
import com.example.billiard.data.remote.dto.LoginRequest
import com.example.billiard.data.remote.dto.LoginResponse
import com.example.billiard.domain.repository.IAuthRepository

class LoginUseCase(
    private val authRepository: IAuthRepository

) {
    suspend operator fun invoke(email: String, password: String): Resource<LoginResponse> {
        val loginRequest = LoginRequest(email, password)
        return authRepository.login(loginRequest)
    }
}