package com.example.billiard


import android.app.Application
import com.example.billiard.di.AppContainer
import com.example.billiard.di.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BilliardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}