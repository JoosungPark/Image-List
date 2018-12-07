package com.joosung.imagelist.ui.home

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joosung.imagelist.R
import com.joosung.imagelist.common.BaseFragment
import com.joosung.imagelist.common.ErrorCatchable
import com.joosung.imagelist.common.FragmentBundle
import com.joosung.imagelist.common.FragmentFactory
import com.joosung.imagelist.databinding.FragmentHomeBinding
import com.joosung.imagelist.extensions.observe
import com.joosung.imagelist.extensions.withViewModel
import com.joosung.imagelist.ui.home.item.HomeImageCell
import com.joosung.imagelist.ui.home.item.HomeLoadingCell
import com.joosung.imagelist.ui.home.item.HomeLoadingCellDelegate
import com.joosung.imagelist.ui.image.ImagePagerFragment
import com.joosung.rxrecycleradapter.RxRecyclerAdapter
import com.joosung.rxrecycleradapter.RxRecyclerAdapterViewHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class HomeFragment : BaseFragment(), ErrorCatchable {

    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by sharedViewModel()

    private lateinit var adapter: RxRecyclerAdapter<HomeCellType>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        withViewModel({ viewModel }) {
            observe(getApiErrorEvent()) { error -> error?.also { handleError(activity, it) } }
            observe(getIsRefreshingEvent()) { value -> value?.also { binding?.refresh?.isRefreshing = it }}
            observe(getTapImageEvent()) { id -> id?.also { openImage(it) }}
        }
        bindList()
    }

    private fun bindList() {
        binding?.also { binding ->
            binding.refresh.setOnRefreshListener { viewModel.load() }
            activity?.also { binding.recycler.layoutManager = LinearLayoutManager(it) }

            adapter = RxRecyclerAdapter(object : RxRecyclerAdapter.Delegate<HomeCellType> {
                override fun getItemViewType(position: Int, item: HomeCellType): Int = when (item) {
                    is HomeCellType.Image -> R.layout.item_home_image
                    is HomeCellType.LoadNext -> R.layout.item_home_loading
                }

                override fun viewHolderForViewType(parent: ViewGroup, viewType: Int): RxRecyclerAdapterViewHolder<HomeCellType> {
                    val viewHolder = when (viewType) {
                        R.layout.item_home_image -> HomeImageCell(inflate(parent, viewType), viewModel, disposables)
                        R.layout.item_home_loading -> HomeLoadingCell(inflate(parent, viewType), loadNextDelegate, disposables)
                        else -> throw RuntimeException("Fatal Error")
                    }

                    @Suppress("UNCHECKED_CAST")
                    return viewHolder as RxRecyclerAdapterViewHolder<HomeCellType>
                }
            })

            binding.recycler.adapter = adapter
            viewModel.dataSourceSubject
                .startWith(Observable.empty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter)
                .addTo(disposables)

            viewModel.load()
        }
    }

    private fun openImage(index: Int) {
        val fragment = FragmentFactory.createFragment(FragmentBundle.ImagePager(index))
        fragment.setTargetFragment(this, ImagePagerFragment.codeLatestIndex)
        pushFragment(fragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ImagePagerFragment.codeLatestIndex && resultCode == Activity.RESULT_OK) {
            val latestIndex = data?.getIntExtra(ImagePagerFragment.kLatestIndex, 0) ?: return
            Observable.just(latestIndex)
                .delay(100, TimeUnit.MILLISECONDS)
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding?.recycler?.layoutManager?.scrollToPosition(it) }
                .addTo(disposables)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private val loadNextDelegate = object : HomeLoadingCellDelegate {
        override fun loadNext() {
            viewModel.loadNext()
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}