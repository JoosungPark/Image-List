package com.joosung.imagelist

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.joosung.imagelist.common.App
import com.joosung.imagelist.common.BaseActivity
import com.joosung.imagelist.common.FragmentBundle
import com.joosung.imagelist.databinding.ActivityMainBinding
import com.joosung.imagelist.http.api.GetImageRequest

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        loadFragment(FragmentBundle.Home)
    }
}
