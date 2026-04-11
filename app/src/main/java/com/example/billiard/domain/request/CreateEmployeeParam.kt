package com.example.billiard.domain.request

// Class này thuộc tầng Domain, hoàn toàn độc lập với các thư viện Data/Network (Gson, Retrofit)
data class CreateEmployeeParam(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: String
)