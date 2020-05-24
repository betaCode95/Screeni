package com.specimen.modular.screenrecord.features.screen_record.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.specimen.modular.channelcatch.callback.ChannelCatchCallback
import com.specimen.modular.channelcatch.manager.ChannelCaptureHelper
import com.specimen.modular.channelcatch.manager.ChannelCatchManager
import com.specimen.modular.channelcatch.service.ChannelCatchForegroundService
import com.specimen.modular.plinth.components.PlinthActivity
import com.specimen.modular.screenrecord.R
import com.specimen.modular.screenrecord.databinding.ActivityChannelCaptureBinding
import kotlinx.android.synthetic.main.activity_channel_capture.*
import java.lang.Exception

class ChannelCaptureActivity : PlinthActivity<ActivityChannelCaptureBinding>() {

    private val channelCaptureHelper by lazy { ChannelCaptureHelper(this, channelCatchCallback) }
    private val channelCatchCallback by lazy {
        object : ChannelCatchCallback {
            override fun askPermission() {

            }

            override fun askForMediaCapturePermission(intent: Intent?, requestCode: Int) {
                this@ChannelCaptureActivity.startActivityForResult(intent, requestCode)
            }

            override fun startedProjection() {
                record.setBackgroundColor(ContextCompat.getColor(this@ChannelCaptureActivity, R.color.colorPrimary))
                record.text = getString(R.string.record_stop)
            }

            override fun stoppedProjection() {
                record.setBackgroundColor(ContextCompat.getColor(this@ChannelCaptureActivity, R.color.green_400))
                record.text = getString(R.string.record_start)
            }

        }
    }

    override fun injectUIEnhancements() {
        super.injectUIEnhancements()
        record.setOnClickListener {
            when (record.text) {
                getString(R.string.record_start) -> {
                    channelCaptureHelper.start(application)
                }
                getString(R.string.record_stop) -> {
                    channelCaptureHelper.end(application)
                }
            }
        }
        downloads.setOnClickListener { channelCaptureHelper.openDownloads() }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
    }

    override fun onViewVisible() {
    }

    override fun onViewRemoved() {
    }

    override fun onViewDestroyed() {
    }

    override fun onViewReVisible() {
    }

    override fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
        channelCaptureHelper.permit(requestCode, resultCode, data)
    }

    override fun contentLayout(): Int = R.layout.activity_channel_capture

}