package com.specimen.modular.channelcatch.utils

import android.media.AudioRecord
import android.os.Environment
import com.specimen.modular.channelcatch.model.RecorderSpecification
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.experimental.and

fun AudioRecord.resume(recorderSpecification: RecorderSpecification): Thread? {
    val s = this
    this.startRecording()
    var audioThread: Thread? = null
    audioThread = thread(start = true) {
        val file = createAudioRecordFile(recorderSpecification)
        audioThread?.let { writeAudioToFile(file, it, s) }
    }
    return audioThread
}

fun AudioRecord.end(audioThread: Thread?) {
    audioThread?.interrupt()
    audioThread?.join()
    this.stop()
    this.release()
}

private fun ShortArray.toByteArray(): ByteArray {
    // Samples get translated into bytes following little-endianness:
    // least significant byte first and the most significant byte last
    val bytes = ByteArray(size * 2)
    for (i in 0 until size) {
        bytes[i * 2] = (this[i] and 0x00FF).toByte()
        bytes[i * 2 + 1] = (this[i].toInt() shr 8).toByte()
        this[i] = 0
    }
    return bytes
}

fun createAudioRecordFile(recorderSpecification: RecorderSpecification): File {
    val uri =
        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${recorderSpecification.a_filePrefix}" +
                "${SimpleDateFormat(
                    recorderSpecification.filePattern,
                    Locale.ENGLISH
                ).format(Date())}${recorderSpecification.a_ext}"
    return File(uri)
}

private fun writeAudioToFile(outputFile: File, thread: Thread, record: AudioRecord) {
    val fileOutputStream = FileOutputStream(outputFile)
    val capturedAudioSamples = ShortArray(1024)

    while (!thread.isInterrupted) {
        record.read(capturedAudioSamples, 0, 1024)

        // This loop should be as fast as possible to avoid artifacts in the captured audio
        // You can uncomment the following line to see the capture samples but
        // that will incur a performance hit due to logging I/O.
        // Log.v(LOG_TAG, "Audio samples captured: ${capturedAudioSamples.toList()}")

        fileOutputStream.write(
            capturedAudioSamples.toByteArray(),
            0,
            1024* 2
        )
    }

    fileOutputStream.close()
}