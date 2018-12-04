package com.joosung.imagelist.ui.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joosung.imagelist.R
import com.joosung.imagelist.common.App
import com.joosung.imagelist.common.BaseFragment
import com.joosung.imagelist.common.ErrorCatchable
import com.joosung.imagelist.databinding.FragmentHomeBinding
import com.joosung.imagelist.extensions.observe
import com.joosung.imagelist.extensions.withViewModel
import com.joosung.imagelist.ui.home.item.HomeImageCell
import com.joosung.imagelist.ui.home.item.HomeLoadingCell
import com.joosung.imagelist.ui.home.item.HomeLoadingCellDelegate
import com.joosung.rxrecycleradapter.RxRecyclerAdapter
import com.joosung.rxrecycleradapter.RxRecyclerAdapterViewHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import org.koin.android.viewmodel.ext.android.sharedViewModel

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
            init()
        }
        bindList()
    }

    private fun bindList() {
        binding?.also { binding ->
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