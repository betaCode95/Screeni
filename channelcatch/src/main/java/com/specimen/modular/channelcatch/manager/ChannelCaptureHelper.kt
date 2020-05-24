package com.specimen.modular.channelcatch.manager

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.specimen.modular.channelcatch.callback.ChannelCatchCallback
import com.specimen.modular.channelcatch.service.ChannelCatchForegroundService
import com.specimen.modular.plinth.components.PlinthActivity

// Public API
class ChannelCaptureHelper(
    private val activity: PlinthActivity<*>,
    private val callback: ChannelCatchCallback
) {

    private var channelCatchService: ChannelCatchForegroundService? = null
    private val channelCatchCallback by lazy { callback }

    private val channelConnection by lazy {
        object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                callback.stoppedProjection()
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                when (service) {
                    is ChannelCatchForegroundService.CustomChannelBinder -> {
                        channelCatchService = service.getService()
                        channelCatchService?.setCallback(channelCatchCallback)
                        callback.startedProjection()
                    }
                }
            }

        }
    }

    fun start(app: Application) {
        if (ContextCompat.checkSelfPermission(
                app,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                app,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            app.bindService(
                Intent(app, ChannelCatchForegroundService::class.java),
                channelConnection,
                Context.BIND_AUTO_CREATE
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                999
            )
        }
    }

    fun end(app: Application) {
        channelCatchService?.stop()
        app.unbindService(channelConnection)
    }

    fun permit(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == channelCatchService?.requestCode()) {
            channelCatchService?.onPermitted(resultCode, data ?: Intent())
        } else {
            Toast.makeText(activity, "Not Permitted", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDownloads() {
        activity.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }
}