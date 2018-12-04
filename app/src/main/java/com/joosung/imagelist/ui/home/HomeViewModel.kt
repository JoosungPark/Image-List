package com.joosung.imagelist.ui.home

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.joosung.imagelist.common.AppServerInterface
import com.joosung.imagelist.common.CompositeDisposablePresentable
import com.joosung.imagelist.common.ImageRepository
import com.joosung.imagelist.http.api.GetImageRequest
import com.joosung.imagelist.http.api.ImageId
import com.joosung.imagelist.util.LogUtil
import com.joosung.library.rx.RxViewModel
import com.joosung.library.vm.SingleLiveEvent
import com.joosung.rxrecycleradapter.RxRecyclerAdapterChangeEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class HomeViewModel(
    private val repo: ImageRepository,
    private val service: HomeImageServerInterface,
    override val disposables: CompositeDisposable
) : RxViewModel(), CompositeDisposablePresentable {

    private val imageIdList = arrayListOf<String>()
    val dataSourceSubject = PublishSubject.create<RxRecyclerAdapterChangeEvent<HomeCellType>>()
    private val apiErrorEvent = SingleLiveEvent<String>()
    fun getApiErrorEvent() = apiErrorEvent

    val isLoading = ObservableField<Boolean>()
    private var latestItemCount = 0

    private val tapImageEvent = SingleLiveEvent<ImageId>()
    fun getTapImageEvent() = tapImageEvent

    fun init() {
        isLoading.set(true)
    }

    fun load() {
        imageIdList.clear()
        isLoading.set(true)
        service.loadImage()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { imageIdList ->
                    val cells = arrayListOf<HomeCellType>()

                    if (imageIdList.isNotEmpty()) {
                        cells.addAll(imageIdList.map { HomeCellType.Image(it, repo) })
                        cells.add(HomeCellType.LoadNext)
                        latestItemCount = cells.size
                        dataSourceSubject.takeIf { !it.hasComplete() }?.onNext(RxRecyclerAdapterChangeEvent.Reloaded(cells))
                    }


                    isLoading.set(false)
                },
                onError = { error ->
                    apiErrorEvent.value = error.message
                    isLoading.set(false)
                }
            )
            .addTo(disposables)
    }

    fun loadNext() {
        service.loadImage()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { imageIdList ->
                    dataSourceSubject.takeIf { !it.hasComplete() }?.onNext(RxRecyclerAdapterChangeEvent.Removed(latestItemCount - 1))
                    val cells = arrayListOf<HomeCellType>()

                    if (imageIdList.isNotEmpty()) {
                        cells.addAll(imageIdList.map { HomeCellType.Image(it, repo) })
                        cells.add(HomeCellType.LoadNext)
                        dataSourceSubject.takeIf { !it.hasComplete() }?.onNext(RxRecyclerAdapterChangeEvent.InsertedRange(latestItemCount - 1, cells))
                        latestItemCount = latestItemCount - 1 + cells.size
                    }
                },
                onError = { error ->
                    apiErrorEvent.value = error.message
                }
            )
            .addTo(disposables)
    }

    fun tapImage(id: ImageId) {
        tapImageEvent.value = id
    }
}

interface HomeImageServerInterface {
    fun loadImage(): Single<ArrayList<ImageId>>
}

class HomeImageServer(private val server: AppServerInterface, val disposables: CompositeDisposable) : HomeImageServerInterface {
    override fun loadImage(): Single<ArrayList<ImageId>> {
        return Single.create { emitter ->
            server.request(GetImageRequest(server.getDefaultCount()))
                .subscribeBy(
                    onNext = {
                        if (!emitter.isDisposed) {
                            it.response.get()?.images?.apply {
                                val list = arrayListOf<ImageId>()
                                forEach { image -> image.id?.apply { list.add(this) } }
                                emitter.onSuccess(list)
                            }
                        }
                    },
                    onError = {
                        emitter.onError(it)
                    })
        }
    }
}