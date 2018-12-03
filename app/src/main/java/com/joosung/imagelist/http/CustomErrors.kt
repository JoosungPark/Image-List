package com.joosung.imagelist.http

enum class ErrorCode(val code: String) {
    INVALID("invalid")
    ;

    companion object {
        fun from(code: String?): ErrorCode = ErrorCode.values().firstOrNull { it.code == code } ?: ErrorCode.INVALID
    }

}