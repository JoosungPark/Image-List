package com.joosung.imagelist.ui.home

import com.joosung.imagelist.common.ImageRepository
import com.joosung.rxrecycleradapter.RxRecyclerAdapterData

sealed class HomeCellType : RxRecyclerAdapterData {
    data class Image(val id: String, val repo: ImageRepository) : HomeCellType()
    object LoadNext : HomeCellType()
}