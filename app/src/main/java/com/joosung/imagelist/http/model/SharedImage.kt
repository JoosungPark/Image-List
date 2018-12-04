package com.joosung.imagelist.http.model

import android.databinding.BaseObservable
import android.databinding.ObservableField
import com.google.gson.annotations.SerializedName
import com.joosung.imagelist.common.AppShared
import com.joosung.imagelist.http.api.ImageId
import com.joosung.imagelist.model.AppJsonObject
import com.joosung.imagelist.model.AppObject
import com.joosung.imagelist.util.LogUtil

class SharedImage(appShared: AppShared, createPlaceHolder: Boolean, placeHolderId: String) : AppJsonObject {
    @SerializedName("id")
    override var id: ImageId? = null

    @SerializedName("urls")
    var urls: Urls? = null

    fun url(): String? {
        return urls?.full
    }

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
    @SerializedName("regular")
    val full: String? = null

}

class AppSharedImage(si: SharedImage, override val appShared: AppShared) : AppObject<SharedImage>, BaseObservable() {
    override val id: String
    val url = ObservableField<String>()
    override val isPlaceHolder = ObservableField<Boolean>()

    val width = ObservableField<Int>()
    val height = ObservableField<Int>()

    init {
        id = si.id ?: ""
        if (si.id == null) {
            isPlaceHolder.set(true)
        }

        si.url()?.apply { url.set(this) }
        si.width?.apply { width.set(this) }
        si.height?.apply { height.set(this) }
    }

    override fun update(data: SharedImage) {
        isPlaceHolder.set(false)
        data.url()?.apply { url.set(this) }
        data.width?.also { width.set(it) }
        data.height?.also { height.set(it) }
    }

    companion object {
        fun createPlaceholder(id: String, appShared: AppShared): AppSharedImage {
            val image = SharedImage(appShared, true, id)
            return AppSharedImage(image, appShared)
        }
    }
}