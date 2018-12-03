package com.joosung.imagelist.common

import android.content.Context
import com.joosung.imagelist.BuildConfig

class AppConfig(context: Context, private val setting: Setting) {
    enum class Setting {
        Toy
        ;


        fun getAuthorization(): String =
            when (this) {
                Toy -> BuildConfig.Authorization
            }

        fun getServerUrl(): String =
                when (this) {
                    Toy -> BuildConfig.API_URL
                }

    }

    val authorization = setting.getAuthorization()
    val serverUrl = setting.getServerUrl()
}