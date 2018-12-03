package com.joosung.imagelist.common

import io.reactivex.disposables.CompositeDisposable

interface CompositeDisposablePresentable {
    val disposables: CompositeDisposable
}