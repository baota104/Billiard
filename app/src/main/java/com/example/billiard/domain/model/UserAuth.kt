package com.example.billiard.domain.model

data class UserAuth(
    val accessToken: String,
    val refreshToken: String,
    val role: String,
    val fullName: String // gop first va last name
)
