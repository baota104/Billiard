package com.example.billiard.di

import com.example.billiard.core.network.AuthInterceptor
import com.example.billiard.data.remote.api.*
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
            level = HttpLoggingInterceptor.Level.BODY 
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging) 
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
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTableApiService(retrofit: Retrofit): TableApiService {
        return retrofit.create(TableApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEmployeeApiService(retrofit: Retrofit): EmployeeApiService {
        return retrofit.create(EmployeeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryApiService(retrofit: Retrofit): CategoryApiService {
        return retrofit.create(CategoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVoucherApiService(retrofit: Retrofit): VoucherApiService {
        return retrofit.create(VoucherApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePriceListApiService(retrofit: Retrofit): PriceListApiService {
        return retrofit.create(PriceListApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideInvoiceApiService(retrofit: Retrofit): InvoiceApiService {
        return retrofit.create(InvoiceApiService::class.java)
    }
}