package com.example.billiard.domain.usecase.auth

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.UserAuth
import com.example.billiard.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor (
    private val authRepository: AuthRepository

) {
    operator fun invoke(username: String, password: String): Flow<Resource<UserAuth>> = flow {
        emit(Resource.Loading)
        val result = authRepository.login(username, password)
        emit(result)
    }
}