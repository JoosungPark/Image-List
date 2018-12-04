package com.joosung.imagelist.common

import android.app.Application
import android.content.Context
import com.joosung.imagelist.BuildConfig
import com.joosung.imagelist.di.imageApp
import com.joosung.imagelist.http.AppServer
import com.joosung.imagelist.model.Persist
import com.joosung.imagelist.preference.ImagePreferences
import com.joosung.imagelist.util.Tag
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(this, imageApp, logger = AndroidLogger(showDebug =  Tag.DEBUG.getValue()))
    }

    companion object {
        fun getPersist(context: Context) = Persist(ImagePreferences(context, BuildConfig.APPLICATION_ID))
    }
}