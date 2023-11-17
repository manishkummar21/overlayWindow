package com.example.overlaywindow.utils

import android.util.Log


object LogUtils {
    private const val TAG = "Overlay:LogUtils"

    fun i(text: String) {
        Log.i(TAG, text)
    }

    fun d(text: String) {
        Log.d(TAG, text)
    }

    fun w(text: String) {
        Log.w(TAG, text)
    }

    fun e(text: String) {
        Log.e(TAG, text)
    }
}