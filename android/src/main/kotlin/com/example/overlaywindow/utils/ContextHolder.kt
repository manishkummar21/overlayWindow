package com.example.overlaywindow.utils

import android.content.Context

object ContextHolder {
    var applicationContext: Context? = null
        set(context) {
            if (applicationContext == null) {
                field = context
                LogUtils.d("received application context")
            }
        }
}