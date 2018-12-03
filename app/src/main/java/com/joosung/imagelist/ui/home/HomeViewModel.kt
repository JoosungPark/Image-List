package com.joosung.imagelist.ui.home

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.joosung.imagelist.common.AppServerInterface
import com.joosung.imagelist.common.CompositeDisposablePresentable
import com.joosung.imagelist.common.ImageRepository
import com.joosung.imagelist.http.api.GetImageRequest
import com.joosung.imagelist.http.api.ImageId
import com.joosung.library.vm.SingleLiveEvent
import com.joosung.rxrecycleradapter.RxRecyclerAdapterChangeEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class HomeViewModel(
    private val repo: ImageRepository,
    private val service: HomeImageServerInterface,
    override val disposables: CompositeDisposable
) : ViewModel(),
    CompositeDisposablePresentable {
    private val imageIdList = arrayListOf<String>()
    val dataSourceSubject = PublishSubject.create<RxRecyclerAdapterChangeEvent<HomeCellType>>()

    private val apiErrorEvent = SingleLiveEvent<String>()
    fun getApiErrorEvent() = apiErrorEvent

    val isLoading = ObservableField<Boolean>()
    private var latestItemCount = 0

    fun init() {
        isLoading.set(true)
    }

    @SuppressLint("CheckResult")
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
    }

    @SuppressLint("CheckResult")
    fun loadMore() {
        service.loadImage()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { imageIdList ->
                    dataSourceSubject.takeIf { !it.hasComplete() }?.onNext(RxRecyclerAdapterChangeEvent.Removed(latestItemCount - 1))
                    val cells = arrayListOf<HomeCellType>()

                    if (imageIdList.isNotEmpty()) {
                        cells.addAll(imageIdList.map { HomeCellType.Image(it, repo) })
                        cells.add(HomeCellType.LoadNext)
                        latestItemCount = cells.size
                        dataSourceSubject.takeIf { !it.hasComplete() }?.onNext(RxRecyclerAdapterChangeEvent.InsertedRange(latestItemCount - 1, cells))
                        latestItemCount = latestItemCount - 1 + cells.size

                    }

                    isLoading.set(false)
                },
                onError = { error ->
                    apiErrorEvent.value = error.message
                    isLoading.set(false)

                }
            )
    }

}

interface HomeImageServerInterface {
    fun loadImage(): Single<ArrayList<ImageId>>
}

class HomeImageServer(private val server: AppServerInterface) : HomeImageServerInterface {
    override fun loadImage(): Single<ArrayList<ImageId>> {
        return Single.create { emitter ->
            server.request(GetImageRequest(server.getDefaultCount()))
                .subscribeBy(
                    onNext = {
                        if (!emitter.isDisposed) {
                            it.response.get()?.images?.apply {
                                val list = arrayListOf<ImageId>()
                                forEach { it.id?.apply { list.add(this) } }
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