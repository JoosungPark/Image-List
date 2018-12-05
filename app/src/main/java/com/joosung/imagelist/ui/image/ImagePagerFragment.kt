package com.joosung.imagelist.ui.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joosung.imagelist.common.BaseFragment
import com.joosung.imagelist.common.ErrorCatchable
import com.joosung.imagelist.http.api.ImageId
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ImagePagerFragment() : BaseFragment(), ErrorCatchable {
    private val list: ArrayList<ImageId> by lazy { arguments?.getStringArrayList(kImageIdList) ?: error("Intent Argument $kImageIdList is missing") }
    private val pagerViewModel: ImagePagerViewModel by viewModel { parametersOf(kImageIdList) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {
        const val kImageIdList = "imageIdList"

        fun newInstance(list: ArrayList<ImageId>): ImagePagerFragment {
            val fragment = ImagePagerFragment()
            val arg = Bundle()
            arg.putStringArrayList(kImageIdList, list)
            fragment.arguments = arg

            return fragment
        }
    }
}