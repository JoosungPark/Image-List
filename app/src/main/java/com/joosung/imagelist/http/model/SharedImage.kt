package com.joosung.imagelist.http.model

import android.databinding.BaseObservable
import android.databinding.ObservableField
import com.google.gson.annotations.SerializedName
import com.joosung.imagelist.common.AppShared
import com.joosung.imagelist.http.api.ImageId
import com.joosung.imagelist.model.AppJsonObject
import com.joosung.imagelist.model.AppObject

class SharedImage(appShared: AppShared, createPlaceHolder: Boolean, placeHolderId: String) : AppJsonObject {
    @SerializedName("id")
    override var id: ImageId? = null

    @SerializedName("urls")
    var urls: Urls? = null

    var url = urls?.full

    @SerializedName("width")
    var width: Int? = null
    @SerializedName("height")
    var height: Int? = null

    init {
        if (createPlaceHolder) {
            id = placeHolderId
        }
    }
}

class Urls {
    @SerializedName("full")
    val full: String? = null
}

class AppSharedImage(si: SharedImage, override val appShared: AppShared) : AppObject<SharedImage>, BaseObservable() {
    override val id: String = si.id!!
    val url = ObservableField(si.url!!)
    override val isPlaceHolder = ObservableField(false)

    val width = ObservableField(si.width!!)
    val height = ObservableField(si.height!!)

    override fun update(data: SharedImage) {
        isPlaceHolder.set(false)
        data.url?.apply { url.set(this) }
    }

    companion object {
        fun createPlaceholder(id: String, appShared: AppShared): AppSharedImage {
            val image = SharedImage(appShared, true, id)
            return AppSharedImage(image, appShared)
        }
    }
}