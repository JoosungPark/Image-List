package com.joosung.imagelist.http

open class ImageThrowable(override val message: String? = "", override val cause: Throwable? = null) : Throwable(message, cause)

class ImageJavaError(private val error: Throwable) : ImageThrowable(error.localizedMessage, error)

class ImageServerError(val errorCode: ErrorCode = ErrorCode.INVALID, val errorMessage: String) : ImageThrowable(errorMessage, Throwable(errorMessage))