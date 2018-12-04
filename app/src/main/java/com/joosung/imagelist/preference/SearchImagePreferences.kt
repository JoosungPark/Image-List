package com.joosung.imagelist.preference

import android.content.Context

class ImagePreferences(private val context: Context, val name: String) : BasePreferences(context, name) {
    companion object {
        val kImageThreshold = "kImageThreshold"
        val imageThresholdDelta = 50
        val imageThresholdMinimum = 200
        val imageThresholdDefault = 750
        val imageMinimumHeight = 200
    }
}