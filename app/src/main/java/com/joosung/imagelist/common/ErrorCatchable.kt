package com.joosung.imagelist.common

import com.joosung.imagelist.http.ImageServerError

interface ErrorCatchable {
    fun handleError(shared: AppShared, error: String) {
        Notifier.toast(error)
    }

    fun handleError(shared: AppShared, error: ImageServerError?) {
        error?.errorMessage?.let { handleError(shared, it) }
    }

    fun handleError(shared: AppShared, throwable: Throwable) {
        handleError(shared, throwable.localizedMessage)
    }
}