package com.example.billiard.domain.usecase.auth

import android.content.Intent
import com.example.billiard.data.remote.dto.LoginResponse
import com.example.billiard.data.repository.IAuthRepository

class LoginUseCase(
    private val repository: IAuthRepository
) {

    suspend fun execute(
        username: String,
        password: String
    ): LoginResponse {

        return repository.login(username, password)
    }
}