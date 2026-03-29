package com.example.billiard.domain.repository

import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.UserAuth

interface AuthRepository {
    suspend fun login(username: String, password: String): Resource<UserAuth>
}