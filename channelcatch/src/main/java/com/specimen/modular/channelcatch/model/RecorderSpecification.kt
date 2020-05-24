package com.specimen.modular.channelcatch.model

import android.media.MediaRecorder

data class RecorderSpecification(
    val width: Int = 720,
    val height: Int = 1280,
    val v_bitrate: Int = 512 * 1000,
    val frameRate: Int = 30,
    val v_encoding: Int = MediaRecorder.VideoEncoder.H264,
    val a_encoding: Int = MediaRecorder.AudioEncoder.AMR_NB,
    val filePattern: String = "dd-MM-yyyy-hh_mm_ss",
    val v_filePrefix: String = "Record_",
    val a_filePrefix: String = "Capture_",
    val v_ext: String = ".mp4",
    val a_ext: String = ".pcm",
    val v_source: Int = MediaRecorder.VideoSource.SURFACE,
    val a_source: Int = MediaRecorder.AudioSource.MIC,
    val outputFormat: Int = MediaRecorder.OutputFormat.THREE_GPP,
    val requestCode: Int = 1001
)