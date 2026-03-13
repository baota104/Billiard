package com.example.billiard.data.remote.auth

import com.example.billiard.data.remote.dto.LoginResponse

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @FormUrlEncoded
    @POST("realms/billiard-management/protocol/openid-connect/token")
    suspend fun login(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse
}