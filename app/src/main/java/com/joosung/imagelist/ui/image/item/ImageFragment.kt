package com.joosung.imagelist.ui.image.item

import android.os.Bundle
import com.joosung.imagelist.common.BaseFragment
import com.joosung.imagelist.http.api.ImageId

class ImageFragment : BaseFragment() {
    companion object {
        const val kImageId = "imageId"

        fun newInstance(id: ImageId): ImageFragment {
            val fragment = ImageFragment()

            val argument = Bundle()
            argument.putString(kImageId, id)
            fragment.arguments = argument

            return fragment
        }
    }
}