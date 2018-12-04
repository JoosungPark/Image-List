package com.joosung.imagelist.common

import android.annotation.SuppressLint
import android.support.annotation.StringRes
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.joosung.imagelist.R
import com.joosung.imagelist.util.LogUtil
import com.joosung.library.rx.delay

class Notifier {
    companion object {
        var toast: Toast? = null

        fun toast(resId: Int, gravity: Int) {
            delay {
                val toast = Toast.makeText(App.get(), App.get().getString(resId), Toast.LENGTH_SHORT)
                toast.setGravity(gravity, 0, App.get().resources.getDimensionPixelSize(R.dimen.Toast_Bottom_Margin))
                toast.show()
            }
        }

        fun toast(@StringRes resId: Int) {
            toast(App.get().getString(resId))
        }

        @SuppressLint("ShowToast")
        fun toast(msg: String?) {
            if (msg == null) {
                LogUtil.e("Toast Error msg is null")
                return
            }
            delay {
                if (toast == null) {
                    toast = Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT)
                } else {
                    toast?.setText(msg)
                }
                toast?.setGravity(Gravity.CENTER, 0, 0)
                toast?.view?.findViewById<TextView>(android.R.id.message)?.gravity = Gravity.CENTER
                toast?.show()
            }
        }
    }
}