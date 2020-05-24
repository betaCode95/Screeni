package com.specimen.modular.channelcatch.service

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.specimen.modular.channelcatch.callback.ChannelCatchCallback
import com.specimen.modular.channelcatch.manager.ChannelCatchManager
import com.specimen.modular.plinth.components.PlinthService
import com.specimen.modular.plinth.utils.notification

class ChannelCatchForegroundService : PlinthService() {

    private var callback: ChannelCatchCallback? = null
    private var channelCatchManager: ChannelCatchManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1010, notification(this))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = CustomChannelBinder()

    fun onPermitted(resultCode: Int, data: Intent) {
        channelCatchManager?.onPermitted(resultCode, data)
    }

    fun setCallback(callback: ChannelCatchCallback?) {
        this.callback = callback
        work()
    }

    fun stop() {
        channelCatchManager?.stop()
        channelCatchManager = null
    }

    private fun work() {
        channelCatchManager = object: ChannelCatchManager(this) {
            override fun askMediaProjectionPermission(intent: Intent?, requestCode: Int) {
                callback?.askForMediaCapturePermission(intent, requestCode)
            }

            override fun startedProjection() {
                callback?.startedProjection()
            }

            override fun stoppedProjection() {
                callback?.stoppedProjection()
            }
        }
        channelCatchManager?.init()
    }

    fun requestCode() = channelCatchManager?.requestCode()

    inner class CustomChannelBinder : Binder() {
        fun getService() = this@ChannelCatchForegroundService
    }
}