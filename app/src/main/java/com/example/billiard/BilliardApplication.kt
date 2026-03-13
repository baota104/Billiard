package com.example.billiard


import android.app.Application
import com.example.billiard.di.AppContainer


class BilliardApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = AppContainer(this)
    }
}