package com.joosung.imagelist.ui.home

import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.support.v4.widget.SwipeRefreshLayout
import com.joosung.imagelist.common.AppServerInterface
import com.joosung.imagelist.common.ImageRepository
import com.joosung.imagelist.http.api.GetImageRequest
import com.joosung.imagelist.http.api.ImageId
import com.joosung.library.rx.RxViewModel
import com.joosung.library.rx.Variable
import com.joosung.library.vm.SingleLiveEvent
import com.joosung.rxrecycleradapter.RxRecyclerAdapterChangeEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class HomeViewModel(
    private val repo: ImageRepository,
    private val service: HomeImageServerInterface
) : RxViewModel() {

    private val imageSource = Variable(arrayListOf<ImageId>())
    fun getImageSource() = imageSource

    val dataSourceSubject = PublishSubject.create<RxRecyclerAdapterChangeEvent<HomeCellType>>()
    private val apiErrorEvent = SingleLiveEvent<String>()
    fun getApiErrorEvent() = apiErrorEvent

    val isLoading = ObservableField<Boolean>(true)
    private var latestItemCount = 0

    private val tapImageEvent = SingleLiveEvent<Int>()
    fun getTapImageEvent() = tapImageEvent

    private val isRefreshingEvent = SingleLiveEvent<Boolean>()
    fun getIsRefreshingEvent() = isRefreshingEvent

    init {
        load()
    }

    fun monitor() {
        launch {
            imageSource
                .asObservable()
                .distinctUntilChanged()
                .subscribe {
                    val previous = latestItemCount - 1
                    dataSourceSubject.takeIf { !it.hasComplete() }?.onNext(RxRecyclerAdapterChangeEvent.Removed(previous))
                    val cells = arrayListOf<HomeCellType>()
                    cells.addAll(it.filterIndexed { index, _ -> index >= previous }.map { HomeCellType.Image(it, repo) })
                    cells.add(HomeCellType.LoadNext)
                    dataSourceSubject.takeIf { !it.hasComplete() }?.onNext(RxRecyclerAdapterChangeEvent.InsertedRange(previous, cells))
                    latestItemCount = previous + cells.size
                }
        }
    }

    fun load() {
        launch {
            latestItemCount = 0
            imageSource.value.clear()
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

                            imageSource.value = imageIdList
                        }

                        isRefreshingEvent.value = false
                        isLoading.set(false)
                    },
                    onError = { error ->
                        apiErrorEvent.value = error.message
                        isRefreshingEvent.value = false
                        isLoading.set(false)
                    }
                )
        }
    }

    fun loadNext() {
        launch {
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

                            val list = arrayListOf<ImageId>()
                            list.addAll(imageSource.value)
                            list.addAll(imageIdList)
                            imageSource.value = list
                        }
                    },
                    onError = { error ->
                        apiErrorEvent.value = error.message
                    }
                )
        }
    }

    fun tapImage(id: ImageId) {
        tapImageEvent.value = imageSource.value.indexOf(id)
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