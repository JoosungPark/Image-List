package com.joosung.imagelist.ui.image

import com.joosung.imagelist.common.FragmentBundle
import com.joosung.imagelist.common.ImageRepository
import com.joosung.imagelist.http.api.ImageId
import com.joosung.imagelist.ui.home.HomeImageServerInterface
import com.joosung.library.rx.RxViewModel
import com.joosung.library.rx.Variable

class ImagePagerViewModel(
    private val imageSource: Variable<ArrayList<ImageId>>,
    private val repo: ImageRepository,
    private val service: HomeImageServerInterface
) : RxViewModel() {

    val dataSource = imageSource.asObservable().map { list ->  list.map { FragmentBundle.Image(it) } }

}