package com.joosung.imagelist.common

import com.joosung.imagelist.ui.home.HomeFragment

/**
 *
 * Created by jei.park on 2017. 12. 26..
 */

sealed class FragmentBundle {
    object Home : FragmentBundle()
//    data class ImagePager(val index: Int) : FragmentBundle()
//    data class Image(val image: ImageModel) : FragmentBundle()
}

class FragmentFactory {

    companion object {
        fun createFragment(bundle: FragmentBundle): BaseFragment = when (bundle) {
                is FragmentBundle.Home -> HomeFragment.newInstance()
//                is ImagePager -> ImagePagerFragment.newInstance(bundle.index)
//                is FragmentBundle.Image -> ImageFragment.newInstance(bundle.image)
        }
    }
}