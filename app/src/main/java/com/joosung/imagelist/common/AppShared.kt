package com.joosung.imagelist.common

import android.content.Context
import com.google.gson.GsonBuilder
import com.joosung.imagelist.http.AppServer
import com.joosung.imagelist.http.ImageRequest
import com.joosung.imagelist.http.ImageResponse
import com.joosung.imagelist.http.model.AppSharedImage
import com.joosung.imagelist.http.model.OptionalTypeAdapter
import com.joosung.imagelist.model.VarDict
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observables.ConnectableObservable


interface AppServerInterface {
    fun <T : ImageResponse> request(request: ImageRequest<T>): ConnectableObservable<ImageRequest<T>>
    fun getDefaultCount(): Int
}

interface ImageRepository {
    fun observeImage(id: String): Observable<AppSharedImage>
    fun getImage(id: String): AppSharedImage?
}

interface AppSharedInterface: AppServerInterface, ImageRepository

class AppShared(config: AppConfig) : AppSharedInterface {
    val gson = GsonBuilder().registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY).create()!!
    val images = VarDict { AppSharedImage.createPlaceholder(it, this) }
    val server = AppServer(this, config)

    override fun observeImage(id: String): Observable<AppSharedImage> = images.observe(id)
    override fun getImage(id: String): AppSharedImage? = images.getOrPlaceholder(id)

    override fun <T : ImageResponse> request(request: ImageRequest<T>): ConnectableObservable<ImageRequest<T>> {
        return server.request(request)
    }

    override fun getDefaultCount(): Int {
        return 20
    }
}