package com.joosung.imagelist.model

import com.joosung.imagelist.preference.BasePreferences
import com.joosung.imagelist.preference.ImagePreferences

class Persist(val preferences: BasePreferences? = null) {
    enum class Key(val key: String) {
        ImageThreshold(ImagePreferences.kImageThreshold)
    }

    val internal = hashMapOf<Key, Any?>()

    init {
        initialize()
    }

    private fun initialize() {
        preferences?.let {
            internal[Key.ImageThreshold] = preferences.getValue(Key.ImageThreshold.key, ImagePreferences.imageThresholdDefault)
        } ?: Key.values().forEach { internal[it] = null }
    }

    inline fun <reified T> write(key: Persist.Key, value: T?) {
        preferences?.let {
            when (key) {
                Persist.Key.ImageThreshold -> it.updateValue(key.key, value as Int?)
//                else -> throw IllegalArgumentException("preference key and value type does not match.")
            }
        }

        value?.let { internal[key] = it }
        ?: internal.remove(key)
    }

    inline fun <reified T> read(key: Persist.Key): T? = internal[key] as T?
}