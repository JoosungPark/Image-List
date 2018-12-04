package com.joosung.imagelist.common

import com.joosung.imagelist.BuildConfig

class AppConfig(private val setting: Setting) {
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