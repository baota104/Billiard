package com.example.billiard.di

import com.example.billiard.core.network.AuthInterceptor
import com.example.billiard.data.remote.api.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // theo doi api
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Gắn trạm thu phí gắn Token vào!
            .addInterceptor(logging) // Gắn camera ở cuối cùng để xem Token đã được gắn chưa
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://26.4.107.54:9090/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        // Retrofit sẽ tự động sinh ra class implement từ interface AuthApiService của bạn
        return retrofit.create(AuthApiService::class.java)
    }

}