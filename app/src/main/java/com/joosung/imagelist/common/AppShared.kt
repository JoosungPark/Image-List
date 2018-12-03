package com.joosung.imagelist.common

import android.content.Context
import com.google.gson.GsonBuilder
import com.joosung.imagelist.http.AppServer
import com.joosung.imagelist.http.model.AppSharedImage
import com.joosung.imagelist.http.model.OptionalTypeAdapter
import com.joosung.imagelist.model.VarDict
import io.reactivex.disposables.CompositeDisposable

class AppShared(val context: Context, val config: AppConfig) : CompositeDisposablePresentable {
    override val disposables = CompositeDisposable()
    val gson = GsonBuilder().registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY).create()!!
    val images = VarDict { AppSharedImage.createPlaceholder(it, this) }
    val server = AppServer(this, config)
}