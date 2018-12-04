package com.joosung.imagelist.di

import com.joosung.imagelist.common.AppConfig
import com.joosung.imagelist.common.AppShared
import com.joosung.imagelist.common.ImageRepository
import com.joosung.imagelist.ui.home.HomeImageServer
import com.joosung.imagelist.ui.home.HomeImageServerInterface
import com.joosung.imagelist.ui.home.HomeViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    viewModel { HomeViewModel(get(), get()) }


    single<ImageRepository>(createOnStart = true) { AppShared(AppConfig(AppConfig.Setting.Toy)) }
    single<HomeImageServerInterface>(createOnStart = true) { HomeImageServer(get()) }
}

val imageApp = listOf(appModule)