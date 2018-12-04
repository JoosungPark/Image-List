package com.joosung.imagelist.common

import android.app.Application
import com.joosung.imagelist.BuildConfig
import com.joosung.imagelist.di.imageApp
import com.joosung.imagelist.http.AppServer
import com.joosung.imagelist.model.Persist
import com.joosung.imagelist.preference.ImagePreferences
import com.joosung.imagelist.util.Tag
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger

class App : Application() {
    var shared: AppShared? = null
    lateinit var persist: Persist
    lateinit var server: AppServer

    override fun onCreate() {
        super.onCreate()

        startKoin(this, imageApp, logger = AndroidLogger(showDebug =  Tag.DEBUG.getValue()))

        app = this
        persist = Persist(ImagePreferences(BuildConfig.APPLICATION_ID))

        shared = AppShared(AppConfig(AppConfig.Setting.Toy))
        server = shared!!.server
    }

    companion object {
        private lateinit var app: App
        fun get(): App = app
    }
}