package com.example.overlaywindow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.example.overlaywindow.services.OverlayWindowService
import com.example.overlaywindow.utils.ContextHolder.applicationContext
import com.example.overlaywindow.utils.LogUtils
import com.example.overlaywindow.utils.Utils
import com.example.overlaywindow.utils.Utils.INTENT_EXTRA_IS_CLOSE_WINDOW
import io.flutter.plugin.common.*


class MethodCallHandlerImpl : MethodChannel.MethodCallHandler,
    PluginRegistry.ActivityResultListener {

    private var requestCode = 100

    var mActivity: Activity? = null
        set(context) {
            if (mActivity == null) {
                field = context
                LogUtils.d("activity is initialized")
            }
        }

    private var channel: MethodChannel? = null

    private val mContext: Context? by lazy { applicationContext }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        LogUtils.d("On method call " + call.method)
        when (call.method) {
            "getPlatformVersion" -> result.success("Android " + Build.VERSION.RELEASE)
            "requestPermissions" -> {
                assert(mContext != null)
                result.success(askPermission())
            }
            "checkPermissions" -> {
                assert(mContext != null)
                result.success(checkPermission())
            }
            "showSystemWindow" -> {
                if (checkPermission()) {
                    LogUtils.d("Going to show System Alert Window")
                    val i = Intent(mContext, OverlayWindowService::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    i.putExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false)
                    mContext?.startService(i)
                } else {
                    Toast.makeText(
                        mContext, "Please give draw over other apps permission", Toast.LENGTH_LONG
                    ).show()
                    result.success(false)
                }
                result.success(true)
            }
            "closeSystemWindow" -> {
                val i = Intent(mContext, OverlayWindowService::class.java)
                i.putExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, true)
                mContext!!.startService(i)
                result.success(true)
            }
        }
    }

    private fun askPermission(): Boolean {
        if (!Settings.canDrawOverlays(mContext)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + mContext?.packageName)
            )
            LogUtils.e("package name is + ${mContext?.packageName}")
            if (mActivity == null) {
                LogUtils.d("activity is null")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mContext?.startActivity(intent)
                Toast.makeText(
                    mContext,
                    "Please grant, Can Draw Over Other Apps permission.",
                    Toast.LENGTH_SHORT
                ).show()
                LogUtils.e("Can't detect the permission change, as the mActivity is null")
            } else {
                LogUtils.d("startActivityForResult")
                mActivity?.startActivityForResult(
                    intent, requestCode
                )
            }
        } else {
            return true
        }
        return false
    }

    private fun checkPermission(): Boolean {
        return Settings.canDrawOverlays(mContext)
    }

    fun startListening(messenger: BinaryMessenger) {
        if (channel != null) {
            LogUtils
                .w("Setting a method call handler before the last was disposed.")
            stopListening()
        }
        channel = MethodChannel(messenger, Utils.CHANNEL, JSONMethodCodec.INSTANCE)
        channel?.setMethodCallHandler(this)
    }

    fun stopListening() {
        if (channel == null) {
            LogUtils.d("Tried to stop listening when no MethodChannel had been initialized.")
            return
        }
        channel?.setMethodCallHandler(null)
        channel = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, p2: Intent?): Boolean {
        if (requestCode == this.requestCode) {
            mContext?.let {
                if (!Settings.canDrawOverlays(it)) {
                    LogUtils.e(
                        "System Alert Window will not work without 'Can Draw Over Other Apps' permission"
                    )
                    Toast.makeText(
                        it,
                        "System Alert Window will not work without 'Can Draw Over Other Apps' permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
        return false
    }
}