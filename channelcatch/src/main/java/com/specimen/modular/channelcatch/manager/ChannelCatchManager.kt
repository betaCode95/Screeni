package com.specimen.modular.channelcatch.manager

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.*
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.specimen.modular.channelcatch.model.RecorderSpecification
import com.specimen.modular.channelcatch.utils.end
import com.specimen.modular.channelcatch.utils.resume
import com.specimen.modular.plinth.components.PlinthActivity
import com.specimen.modular.plinth.components.PlinthService
import com.specimen.modular.plinth.utils.screenDensity
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

abstract class ChannelCatchManager(
    private val service: PlinthService,
    private val recordSpec: RecorderSpecification = RecorderSpecification(),
    private val callback: MediaProjection.Callback? = null
) {

    private val mediaProjectionManager by lazy { service.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager }
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var audioRecord: AudioRecord? = null
    private var audioRecordThread: Thread? = null
    private val mediaProjectionCallback by lazy {
        object : MediaProjection.Callback() {
            override fun onStop() {
                try {
                    stop()
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                    e.printStackTrace()
                }
                super.onStop()
            }
        }
    }
    private val mediaRecorder by lazy { MediaRecorder() }
    private var isPrepared = false

    @Throws(IOException::class)
    private fun initRecorderProps() {
        if (isPrepared) return
        mediaRecorder.setAudioSource(recordSpec.a_source)
        mediaRecorder.setVideoSource(recordSpec.v_source)
        mediaRecorder.setOutputFormat(recordSpec.outputFormat)
        val uri =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${recordSpec.v_filePrefix}" +
                    "${SimpleDateFormat(
                        recordSpec.filePattern,
                        Locale.ENGLISH
                    ).format(Date())}${recordSpec.v_ext}"
        mediaRecorder.setOutputFile(uri)
        mediaRecorder.setVideoSize(recordSpec.width, recordSpec.height)
        mediaRecorder.setVideoEncoder(recordSpec.v_encoding)
        mediaRecorder.setAudioEncoder(recordSpec.a_encoding)
        mediaRecorder.setVideoEncodingBitRate(recordSpec.v_bitrate)
        mediaRecorder.prepare()
        isPrepared = true
    }

    private fun initProjection() {
        if (mediaProjection == null) {
            askMediaProjectionPermission(
                mediaProjectionManager?.createScreenCaptureIntent(),
                recordSpec.requestCode
            )
            return
        }
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            service.javaClass.simpleName,
            recordSpec.width,
            recordSpec.height,
            service.screenDensity(),
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mediaRecorder.surface,
            null,
            null
        )
        mediaRecorder.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            initPlaybackCapture()
        }
        startedProjection()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initPlaybackCapture() {
        if (mediaProjection == null) return
        val builder: AudioPlaybackCaptureConfiguration.Builder =
            AudioPlaybackCaptureConfiguration.Builder(mediaProjection!!)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
        audioRecord = AudioRecord.Builder()
            .setAudioPlaybackCaptureConfig(builder.build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(8000)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build())
            .setBufferSizeInBytes(AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT))
            .build()
        audioRecordThread = audioRecord?.resume(recordSpec)
    }


    // Public API

    fun init() {
        try {
            initRecorderProps()
            initProjection()
        } catch (e: Exception) {
            Log.e("Error", e.toString())
            e.printStackTrace()
        }
    }

    @Throws(IllegalStateException::class)
    fun stop() {
        mediaRecorder.stop()
        mediaRecorder.reset()
        virtualDisplay?.release()
        mediaProjection?.unregisterCallback(mediaProjectionCallback)
        audioRecord?.end(audioRecordThread)
        audioRecord = null
        mediaProjection?.stop()
        mediaProjection = null
        callback?.onStop()
        isPrepared = false
        stoppedProjection()
    }

    fun requestCode() = recordSpec.requestCode

    fun onPermitted(resultCode: Int, data: Intent) {
        mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)
        mediaProjection?.registerCallback(mediaProjectionCallback, null)
        initProjection()
    }

    abstract fun askMediaProjectionPermission(intent: Intent?, requestCode: Int)
    abstract fun startedProjection()
    abstract fun stoppedProjection()
}