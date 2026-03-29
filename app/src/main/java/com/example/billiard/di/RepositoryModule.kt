package com.example.billiard.di

import com.example.billiard.data.repository.AuthRepositoryImpl
import com.example.billiard.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // @Binds BẮT BUỘC phải là abstract class và abstract function
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl // Hilt đã biết cách tạo thằng này
    ): AuthRepository // Trả về Interface này

}