package com.example.billiard


import android.app.Application
import com.example.billiard.di.AppContainer
import com.example.billiard.di.DefaultAppContainer


class BilliardApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}