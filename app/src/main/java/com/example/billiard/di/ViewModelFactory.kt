package com.example.billiard.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.billiard.presentation.auth.LoginViewModel

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(container.loginUseCase) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}