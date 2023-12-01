package com.example.overlaywindow

import com.example.overlaywindow.utils.ContextHolder.applicationContext
import com.example.overlaywindow.utils.LogUtils
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


class OverlayWindowPlugin : FlutterPlugin, ActivityAware {

    private var isInitialized = false
    private var methodCallHandler: MethodCallHandlerImpl? = null
    private var pluginBinding: ActivityPluginBinding? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPluginBinding) {
        initialize(flutterPluginBinding)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        if (!isInitialized) {
            LogUtils.d("Already detached from the engine.")
            return;
        }
        LogUtils.d("On detached from engine")
        dispose()
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        LogUtils.d("Initializing on attached to activity")
        methodCallHandler?.let {
            it.mActivity = activityPluginBinding.activity;
        }
        pluginBinding = activityPluginBinding;
        registerListeners()
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        onAttachedToActivity(activityPluginBinding)
    }

    override fun onDetachedFromActivity() {
        LogUtils.d("On detached from activity")
        methodCallHandler?.mActivity = null
        deregisterListeners();
    }

    private fun initialize(binding: FlutterPluginBinding) {
        applicationContext = binding.applicationContext
        if (!isInitialized) {
            isInitialized = true
            LogUtils.d("Initializing on attached to engine")
            if (methodCallHandler == null) {
                methodCallHandler = MethodCallHandlerImpl()
                methodCallHandler?.startListening(binding.binaryMessenger)
            }
            LogUtils.d("onAttachedToEngine")
        }
    }

    private fun dispose() {
        LogUtils.d("Disposing call track plugin class")
        methodCallHandler?.let {
            it.stopListening()
            it.mActivity = null
        }
        applicationContext = null
        methodCallHandler = null
        isInitialized = false
    }

    private fun registerListeners() {
        methodCallHandler?.let { pluginBinding?.addActivityResultListener(it) }
    }

    private fun deregisterListeners() {
        methodCallHandler?.let { pluginBinding?.removeActivityResultListener(it) }
    }
}
