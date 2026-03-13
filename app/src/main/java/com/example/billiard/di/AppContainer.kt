package com.example.billiard.di

import android.content.Context
import com.example.billiard.core.network.AuthInterceptor
import com.example.billiard.core.utils.TokenManager
import com.example.billiard.data.remote.auth.AuthApiService
import com.example.billiard.data.repository.AuthRepositoryImpl
import com.example.billiard.data.repository.IAuthRepository

import com.example.billiard.domain.usecase.auth.LoginUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // Nếu dùng máy ảo thì 10.0.2.2, nếu điện thoại thật và cùng mạng Wi-Fi thì đổi thành IPv4 của máy tính
    private val baseUrl = "http://192.168.110.80:8080/"

    val tokenManager = TokenManager(context)

    private val okHttpClient: OkHttpClient by lazy {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val authRepository: IAuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }

    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }
}