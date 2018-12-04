package com.joosung.imagelist.di

import com.joosung.imagelist.common.*
import com.joosung.imagelist.ui.home.HomeImageServer
import com.joosung.imagelist.ui.home.HomeImageServerInterface
import com.joosung.imagelist.ui.home.HomeViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    val appShared = AppShared(AppConfig(AppConfig.Setting.Toy))

    single<AppSharedInterface>(createOnStart = true) { appShared }
    single<AppServerInterface>(createOnStart = true) { appShared }
    single<ImageRepository>(createOnStart = true) { appShared }
    single<HomeImageServerInterface>(createOnStart = true) { HomeImageServer(get()) }

    viewModel { HomeViewModel(get(), get()) }
}

val imageApp = listOf(appModule)