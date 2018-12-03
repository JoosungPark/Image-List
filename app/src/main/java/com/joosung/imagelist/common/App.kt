package com.joosung.imagelist.common

import android.app.Application
import com.joosung.imagelist.BuildConfig
import com.joosung.imagelist.http.AppServer
import com.joosung.imagelist.model.Persist
import com.joosung.imagelist.preference.ImagePreferences

class App : Application() {
    var shared: AppShared? = null
    lateinit var persist: Persist
    lateinit var server: AppServer

    override fun onCreate() {
        super.onCreate()
        app = this
        persist = Persist(ImagePreferences(BuildConfig.APPLICATION_ID))

        shared = AppShared(this, AppConfig(this, AppConfig.Setting.Toy))
        server = shared!!.server
    }

    companion object {
        private lateinit var app: App
        fun get(): App = app
    }
}