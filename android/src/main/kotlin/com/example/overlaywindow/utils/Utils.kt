package com.example.overlaywindow.utils

import android.content.Context
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor

object Utils {
    const val CHANNEL = "overlay_window"
    const val overLayEngine = "overlayEngine"
    const val INTENT_EXTRA_IS_CLOSE_WINDOW = "IsCloseWindow"


    fun createAndSaveEngineToCache(applicationContext: Context) {
        val enn = FlutterEngineGroup(applicationContext)
        val dEntry = DartExecutor.DartEntrypoint(
            FlutterInjector.instance().flutterLoader().findAppBundlePath(),
            "overlayMain"
        )
        val engine = enn.createAndRunEngine(applicationContext, dEntry)
        FlutterEngineCache.getInstance().put(overLayEngine, engine)
    }
}
