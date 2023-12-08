package com.example.overlaywindow.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.overlaywindow.OverlayWindowPlugin
import com.example.overlaywindow.R
import com.example.overlaywindow.utils.ContextHolder
import com.example.overlaywindow.utils.LogUtils
import com.example.overlaywindow.utils.Utils
import com.example.overlaywindow.utils.Utils.INTENT_EXTRA_IS_CLOSE_WINDOW
import com.example.overlaywindow.utils.Utils.overLayEngine
import io.flutter.embedding.android.FlutterTextureView
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache


class OverlayWindowService : Service() {
    private val notificationID = 1
    private val channelID = "ForegroundServiceChannel"
    private var flutterView: FlutterView? = null
    private val windowManager: WindowManager by lazy {
        getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private val windowLayoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            format = PixelFormat.TRANSLUCENT
            type =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate() {
        LogUtils.d("Creating the overlay window service")
        createNotificationChannel()
        val notificationIntent = Intent(this, OverlayWindowPlugin::class.java)
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                0, notificationIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        val notification: Notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Overlay window service is running")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(getCloseIntent())
            .addAction(0, "stop", getCloseIntent())
            .build()
        try {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(notificationID, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(notificationID, notification)
            }
        } catch (ex: Exception) {
            LogUtils.e("startForeground exception")
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.extras != null) {
            LogUtils.d("Overlay window onStartCommand")
            ContextHolder.applicationContext = this
            val isCloseWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false)
            if (!isCloseWindow) {
                createWindow()
            } else {
                closeWindow(true)
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )!!
            manager.createNotificationChannel(serviceChannel)
        }
    }


    override fun onDestroy() {
        LogUtils.d("Destroying the overlay window service")
        val manager = getSystemService(
            NotificationManager::class.java
        )!!
        manager.cancel(notificationID)
    }

    private fun createWindow() {
        closeWindow(false)
        setFlutterView()
        windowManager.addView(flutterView, windowLayoutParams)
    }

    private fun closeWindow(isStopService: Boolean) {
        LogUtils.i("Closing the overlay window")
        flutterView?.let {
            windowManager.removeView(it)
            it.detachFromFlutterEngine()
            flutterView = null
        }
        if (isStopService) {
            stopSelf()
        }
    }

    private fun setFlutterView() {
        flutterView =
            FlutterView(applicationContext, FlutterTextureView(applicationContext)).apply {
                attachToFlutterEngine(getFlutterEngine())
                fitsSystemWindows = true
            }
    }


    private fun getFlutterEngine(): FlutterEngine {
        if (FlutterEngineCache.getInstance().contains(overLayEngine))
            return FlutterEngineCache.getInstance().get(overLayEngine)!!.apply {
                lifecycleChannel.appIsResumed()
            }
        else {
            Utils.createAndSaveEngineToCache(applicationContext)
            return getFlutterEngine()
        }
    }

    private fun getCloseIntent(): PendingIntent {
        val deleteIntent = Intent(this, OverlayWindowService::class.java).apply {
            putExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, true)
        }
        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getService(this, 1, deleteIntent, pendingFlags)

    }
}