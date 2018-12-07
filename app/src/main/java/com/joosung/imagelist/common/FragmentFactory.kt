package com.joosung.imagelist.common

import com.joosung.imagelist.http.api.ImageId
import com.joosung.imagelist.ui.home.HomeFragment
import com.joosung.imagelist.ui.image.ImagePagerFragment
import com.joosung.imagelist.ui.image.item.ImageFragment

sealed class FragmentBundle {
    object Home : FragmentBundle()
    data class ImagePager(val index: Int) : FragmentBundle()
    data class Image(val id: ImageId) : FragmentBundle()
}

class FragmentFactory {

    companion object {
        fun createFragment(bundle: FragmentBundle): BaseFragment = when (bundle) {
                is FragmentBundle.Home -> HomeFragment.newInstance()
                is FragmentBundle.ImagePager -> ImagePagerFragment.newInstance(bundle.index)
                is FragmentBundle.Image -> ImageFragment.newInstance(bundle.id)
        }
    }
}