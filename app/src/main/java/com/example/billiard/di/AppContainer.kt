// SỬA LẠI DÒNG NÀY CHO ĐÚNG TÊN DỰ ÁN CỦA BẠN
package com.example.billiard.di

import android.content.Context
import com.example.billiard.core.utils.Constants
import com.example.billiard.core.utils.TokenManager
import com.example.billiard.data.remote.api.AuthApiService
import com.example.billiard.data.repository.AuthRepositoryImpl
import com.example.billiard.domain.repository.IAuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val authRepository: IAuthRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = Constants.BASE_URL

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
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
    private val tokenManager: TokenManager by lazy {
        TokenManager(context)
    }

    override val authRepository: IAuthRepository by lazy {
        AuthRepositoryImpl(authApi, tokenManager)
    }
}