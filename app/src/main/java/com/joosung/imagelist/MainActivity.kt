package com.joosung.imagelist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.joosung.imagelist.common.App
import com.joosung.imagelist.http.api.GetImageRequest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = GetImageRequest()
        App.get().shared?.server?.request(request)?.subscribe()
    }
}
