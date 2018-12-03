package com.joosung.imagelist.http

import com.google.gson.annotations.SerializedName
import com.joosung.imagelist.common.AppShared
import com.joosung.imagelist.http.model.SharedImage
import java.util.HashMap

interface ImageResponse {
    val errorCode: String?
    val errorMessage: String?
}

abstract class CommonImageResponse : ImageResponse {
    protected val DEBUG_TAG = this.javaClass.simpleName

    @SerializedName("errorCode")
    override val errorCode: String? = null

    @SerializedName("errorMessage")
    override val errorMessage: String? = null

    open fun processResult(shared: AppShared) {
    }
}
