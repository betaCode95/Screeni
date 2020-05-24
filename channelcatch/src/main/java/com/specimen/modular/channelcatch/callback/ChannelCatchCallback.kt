package com.specimen.modular.channelcatch.callback

import android.content.Intent
import com.specimen.modular.channelcatch.service.ChannelCatchForegroundService

interface ChannelCatchCallback {
    fun askPermission()
    fun askForMediaCapturePermission(intent: Intent?, requestCode: Int)
    fun startedProjection()
    fun stoppedProjection()
}