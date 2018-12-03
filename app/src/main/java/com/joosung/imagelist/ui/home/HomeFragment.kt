package com.joosung.imagelist.ui.home

import com.joosung.imagelist.common.App
import com.joosung.imagelist.common.BaseFragment

class HomeFragment : BaseFragment() {

    init {
        val shared = App.get().shared!!
        val server = HomeImageServer(shared)
        homeViewModelFactory = HomeViewModel(shared, server, disposables)
    }

    companion object {
        lateinit var homeViewModelFactory: HomeViewModel
        fun newInstance() = HomeFragment()
    }
}