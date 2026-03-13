package com.example.billiard.data.repository

import android.content.Intent
import com.example.billiard.data.remote.dto.LoginResponse
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

interface IAuthRepository {

    suspend fun login(
        username: String,
        password: String
    ): LoginResponse
}