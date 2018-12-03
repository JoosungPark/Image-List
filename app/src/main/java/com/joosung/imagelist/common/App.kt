package com.joosung.imagelist.common

import android.app.Application

class App : Application() {
    var shared: AppShared? = null

    override fun onCreate() {
        super.onCreate()
        app = this
        shared = AppShared(this, AppConfig(this, AppConfig.Setting.Toy))
    }

    companion object {
        private lateinit var app: App
        fun get(): App = app
    }
}