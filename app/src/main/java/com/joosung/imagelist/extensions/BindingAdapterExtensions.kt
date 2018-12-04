package com.joosung.imagelist.extensions

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.joosung.imagelist.R
import com.joosung.imagelist.common.App
import com.joosung.imagelist.model.Persist
import com.joosung.imagelist.preference.ImagePreferences
import com.joosung.imagelist.util.LogUtil
import com.joosung.library.rx.delay
import com.squareup.picasso.Picasso

private val imagePlaceHolder = R.color.basic_divider
private val imageBaseColor = android.R.color.black

@BindingAdapter("imageUrl", "imageWidth", "imageHeight")
fun loadImage(view: ImageView, url: String?, width: Int?, height: Int?) {
    if (width == null || height == null) { return }
    val threshold = App.get().persist.read<Int>(Persist.Key.ImageThreshold) ?: ImagePreferences.imageThresholdDefault
    val scaledSize = recommendSize(width, height, threshold, ImagePreferences.imageMinimumHeight)

    delay { view.layoutParams.height = scaledSize.second }

    url?.let {
        Picasso.with(view.context)
            .load(it)
            .resize(scaledSize.first, scaledSize.second)
            .placeholder(imagePlaceHolder)
            .error(imagePlaceHolder)
            .into(view)
    } ?: run {
        Picasso.with(view.context)
            .load(imagePlaceHolder)
            .placeholder(imagePlaceHolder)
            .error(imagePlaceHolder)
            .into(view)
    }
}

@BindingAdapter("fullImageUrl")
fun loadFullPhotoImage(view: ImageView, url: String?) {
    Picasso.with(view.context)
        .load(url)
        .error(imageBaseColor)
        .placeholder(imageBaseColor)
        .into(view)
}

private fun recommendSize(originWidth: Int, originHeight: Int, threshold: Int, minHeight: Int): Pair<Int, Int> {
    return if (originWidth < threshold && originHeight < threshold) Pair(originWidth, originHeight)
    else {
        var scaledSize = Pair(originWidth, originHeight)
        val delta = 0.01
        var index = 99

        while (scaledSize.second > minHeight && scaledSize.first > threshold || scaledSize.second > threshold) {
            val scaledWidth = (originWidth * index * delta).toInt()
            val scaledHeight = (originHeight * index * delta).toInt()
            scaledSize = Pair(scaledWidth, scaledHeight)
            index -= 1
        }

        scaledSize
    }
}
