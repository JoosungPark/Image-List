package com.joosung.imagelist.http

import android.annotation.SuppressLint
import com.joosung.imagelist.BuildConfig
import com.joosung.imagelist.common.AppConfig
import com.joosung.imagelist.common.CompositeDisposablePresentable
import com.joosung.imagelist.common.AppShared
import com.joosung.imagelist.util.LogUtil
import com.joosung.imagelist.util.Tag
import com.joosung.library.rx.Variable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

class AppServer(val appShared: AppShared, private val config: AppConfig): CompositeDisposablePresentable {
    override val disposables = CompositeDisposable()
    private val client: OkHttpClient
    private val gson = appShared.gson
    private val networkInProgress = Variable<HashMap<String, Any>>(hashMapOf())

    private val authorization = config.authorization
    private val JSONMediaType = MediaType.parse("application/json; charset=utf-8")

    init {
        val clientBuilder = OkHttpClient.Builder()
        if (Tag.AppServer.getValue()) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logger)
        }

        clientBuilder.writeTimeout(30, TimeUnit.SECONDS)
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        client = clientBuilder.build()
        client.dispatcher().maxRequestsPerHost = 5
    }

    @SuppressLint("CheckResult")
    fun <T : ImageResponse> request(request: ImageRequest<T>, isCache: Boolean = true): ConnectableObservable<ImageRequest<T>> {
        val subject = ReplaySubject.create<ImageRequest<T>>()

        request.isNetworking.set(true)
        val token = request.uniqueToken
        token?.let {
            if (token.isNotEmpty() && isCache) {
                networkInProgress.get()[token]?.let { stream ->
                    @Suppress("UNCHECKED_CAST")
                    subject.onNext(stream as ImageRequest<T>)
                    subject.onComplete()
                }
            }
        }

        request.requestState.asObservable()
            .filter { it !is RequestInitializing }
            .take(1)
            .subscribeOn(Schedulers.computation())
            .subscribe {
                when (it) {
                    is RequestError -> {
                        request.isNetworking.set(false)
                        subject.onError(ImageJavaError(it.error))
                    }
                    is RequestReady -> {
                        val url = config.serverUrl + request.url
                        LogUtil.i(Tag.AppServer, "request [$url]")

                        var builder = Request.Builder().url(url)
                        builder = when (request.method) {
                            HTTPMethod.get -> builder.get()
                            HTTPMethod.post -> {
                                LogUtil.i(Tag.AppServer, "getting post param)")
                                val param = request.getParams()
                                LogUtil.i(Tag.AppServer, "getting post param 2")
                                val json = if (param != null) gson.toJson(param) else "{}"
                                LogUtil.i(Tag.AppServer, "request body : $json")
                                builder.post(RequestBody.create(JSONMediaType, json))
                            }
                        }

                        builder.addHeader("Authorization", authorization)
                        request.header.forEach { builder.addHeader(it.key, it.value) }
                        val call = builder.build()

                        client.newCall(call).enqueue(object : Callback {
                            override fun onFailure(call: Call?, e: IOException?) {
                                e?.let {
                                    try {
                                        it.printStackTrace()
                                        subject.onError(ImageJavaError(it))
                                    } finally {
                                        token?.let { networkInProgress.value.remove(it) }
                                    }
                                }
                            }

                            override fun onResponse(call: Call?, response: Response?) {
                                val stream = response?.body()?.charStream()
                                stream?.apply {
                                    try {
                                        val json = "{\"images\":${this.readText()}}"
                                        val res: T = gson.fromJson(json, request.responseType)

                                        if (res.errorCode != null && res.errorMessage != null) {
                                            subject.onError(ImageServerError(ErrorCode.from(res.errorCode), res.errorMessage!!))
                                        } else {
                                            request.processResult(appShared, res)
                                            request.response.set(res)
                                            subject.onNext(request)
                                            request.isNetworking.set(false)
                                            subject.onComplete()
                                        }
                                    } catch (ex: Exception) {
                                        if (BuildConfig.DEBUG) {
                                            throw ex
                                        }
                                        ex.printStackTrace()
                                        subject.onError(ImageJavaError(ex))
                                        request.isNetworking.set(false)
                                    } finally {
                                        close()
                                    }
                                }

                            }
                        })
                    }
                }
            }

        token?.let { networkInProgress.get()[token] = request }

        val broadcast = subject.publish()
        broadcast.connect()
        return broadcast
    }
}