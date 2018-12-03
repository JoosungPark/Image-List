package com.joosung.imagelist.ui.home

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.joosung.imagelist.common.AppServerInterface
import com.joosung.imagelist.common.AppShared
import com.joosung.imagelist.common.CompositeDisposablePresentable
import com.joosung.imagelist.common.ImageRepository
import com.joosung.imagelist.http.api.GetImageRequest
import com.joosung.imagelist.http.api.ImageId
import com.joosung.library.vm.SingleLiveEvent
import com.joosung.rxrecycleradapter.RxRecyclerAdapterChangeEvent
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import rx.subjects.PublishSubject

class HomeViewModel(private val repo: ImageRepository, private val server: HomeImageServerInterface, override val disposables: CompositeDisposable) : ViewModel(),
    CompositeDisposablePresentable {
    private val imageIdList = arrayListOf<String>()
    val dataSourceSubject = PublishSubject.create<RxRecyclerAdapterChangeEvent<HomeCellType>>()

    private val apiErrorEvent = SingleLiveEvent<String>()
    fun getApiErrorEvent() = apiErrorEvent

    val isLoading = ObservableField<Boolean>()


    fun init() {
        isLoading.set(true)
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