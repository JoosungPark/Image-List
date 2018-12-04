package com.joosung.imagelist.ui.home.item

import android.view.View
import com.joosung.imagelist.databinding.ItemHomeImageBinding
import com.joosung.imagelist.ui.home.HomeCellType
import com.joosung.imagelist.ui.home.HomeViewModel
import com.joosung.rxrecycleradapter.RxRecyclerAdapterViewHolder
import io.reactivex.disposables.CompositeDisposable

class HomeImageCell(
    itemView: View,
    private val viewModel: HomeViewModel,
    parentDisposable: CompositeDisposable
) : RxRecyclerAdapterViewHolder<HomeCellType.Image>(itemView, parentDisposable) {

    override fun onBindItem(item: HomeCellType.Image, position: Int) {
        super.onBindItem(item, position)

        (binding as? ItemHomeImageBinding)?.also {
            it.viewModel = viewModel
            it.image = item.repo.getImage(item.id)
        }
    }
}