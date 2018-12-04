package com.joosung.imagelist.ui.home.item

import android.view.View
import com.joosung.imagelist.ui.home.HomeCellType
import com.joosung.rxrecycleradapter.RxRecyclerAdapterViewHolder
import io.reactivex.disposables.CompositeDisposable

class HomeLoadingCell(
    itemView: View,
    private val delegate: HomeLoadingCellDelegate,
    disposable: CompositeDisposable
) : RxRecyclerAdapterViewHolder<HomeCellType.LoadNext>(itemView, disposable) {

    override fun onBindItem(item: HomeCellType.LoadNext, position: Int) {
        super.onBindItem(item, position)
        delegate.loadNext()
    }
}

interface HomeLoadingCellDelegate {
    fun loadNext()
}