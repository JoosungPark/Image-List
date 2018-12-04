package com.joosung.imagelist.ui.home.item

import android.view.View
import com.joosung.imagelist.databinding.ItemHomeImageBinding
import com.joosung.imagelist.ui.home.HomeCellType
import com.joosung.imagelist.ui.home.HomeViewModel
import com.joosung.rxrecycleradapter.RxRecyclerAdapterViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class HomeImageCell(
    itemView: View,
    private val viewModel: HomeViewModel,
    private val parentDisposable: CompositeDisposable
) : RxRecyclerAdapterViewHolder<HomeCellType.Image>(itemView, parentDisposable) {

    override fun onBindItem(item: HomeCellType.Image, position: Int) {
        super.onBindItem(item, position)

        (binding as? ItemHomeImageBinding)?.also { binding ->
            binding.viewModel = viewModel
            item.repo.observeImage(item.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding.image = it }
                .addTo(parentDisposable)
        }
    }
}