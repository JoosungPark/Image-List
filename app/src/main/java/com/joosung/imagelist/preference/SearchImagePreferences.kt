package com.joosung.imagelist.preference

import com.joosung.imagelist.preference.BasePreferences

class ImagePreferences(val name: String) : BasePreferences(name) {
    companion object {
        val kImageThreshold = "kImageThreshold"
        val imageThresholdDelta = 50
        val imageThresholdMinimum = 200
        val imageThresholdDefault = 750
        val imageMinimumHeight = 200
    }
}