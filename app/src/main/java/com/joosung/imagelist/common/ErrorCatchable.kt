package com.joosung.imagelist.common

import android.content.Context
import android.widget.Toast
import com.joosung.imagelist.http.ImageServerError

interface ErrorCatchable {
    fun handleError(context: Context?, error: String) {
        context?.also { Toast.makeText(it, error, Toast.LENGTH_SHORT).show() }
    }

    fun handleError(context: Context?, error: ImageServerError?) {
        error?.errorMessage?.let { handleError(context, it) }
    }

    fun handleError(context: Context?, throwable: Throwable) {
        handleError(context, throwable.localizedMessage)
    }
}