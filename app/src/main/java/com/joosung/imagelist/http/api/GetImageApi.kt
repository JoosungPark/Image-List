package com.joosung.imagelist.http.api

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.joosung.imagelist.common.AppShared
import com.joosung.imagelist.http.CommonImageResponse
import com.joosung.imagelist.http.HTTPMethod
import com.joosung.imagelist.http.ImageCommonRequest
import com.joosung.imagelist.http.model.SharedImage
import java.lang.reflect.Type

class GetImageRequest(count: Int = 20) : ImageCommonRequest<GetImageResponse>() {
    override val responseType: Type get() = object : TypeToken<GetImageResponse>() {}.type
    override val method: HTTPMethod get() = HTTPMethod.get
    override var url: String = "photos/random?count=$count"
    override val uniqueToken: String? get() = "${GetImageRequest::class.java.simpleName}_${index++}"

    companion object {
        var index = 0
    }

    override fun processResult(shared: AppShared, data: GetImageResponse) {
        data.processResult(shared)
    }
}

class GetImageResponse : CommonImageResponse() {
    @SerializedName("images")
    var images: ArrayList<SharedImage>? = null

    override fun processResult(shared: AppShared) {
        super.processResult(shared)

        images?.apply {
            shared.images.update(this, shared)
        }
    }
}
