# Screeni [In Works]

A module that helps in capturing Screen, Mic Audio, Internal Audio
* [Overview](#overview-)
* [Architecture](#architecture-)
* [Features](#features-)
* [Modules](#modules-)
* [Configure](#configure-)
* [FAQ](#faq-)
* [Contributing](#contributing-)

## Overview 👣
We shall follow a Modular Mechanism to implement the flow of the App. This makes the App easier to scale

## Architecture 🧰
Minimal/Straightforward architecture as this is supposed to be a demo app

## Features 🧰

| Feature Name  | Description                                                                                   |
| ------------- | --------------------------------------------------------------------------------------------- |
| `screen_record` | Has a ChannelCatchActivity responsible for channelcatch Module and implement Screen Recording |

## Modules 🧰
| Module Name              | Description                                                                                                     |
| ------------------------ | --------------------------------------------------------------------------------------------------------------- |
| `app`                      | This is the main of the Application, this run and keeps activities intact                                       |
| `channelcatch`             | This is the module responsible for recording screen, internal audio, mic audio based on the Current API Version |
| `plinth`                   | Base Components Supplier for the whole app                                                                      |
| bucker (not implemented) | A Logger that logs all crashes, debugs, analytics, etc                                                          |
| `stockpile`                | Repo for all the UI templates                                                                                   |

## Configure 🎨

`channelcatch` module is a independent module and can be used in any app. Can be made public and used out of the box for any app. How to implement `channelcatch` module?

Following needs to be added in the app's `AndroidManifest` root

```xml
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

```xml
    <service
        android:name="com.specimen.modular.channelcatch.service.ChannelCatchForegroundService"
        android:foregroundServiceType="mediaProjection" />
```

Following needs to be added in your activity
```kotlin
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

```

Start Capturing Screen
```kotlin
	channelCaptureHelper.start(application)
```

End Capturing Screen
```kotlin
	channelCaptureHelper.end(application)
```

Default `RecordSpeification`. You can create a new `RecordSpecification` object to change any of these
```kotlin
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
```


## FAQ ❓
* **Why is it not working for me?** - dependency issue maybe, create an issue if it doesn't work
* **Will it support other types of media?** - Yes, all kinds but that will take time

## Contributing 🤝
**Open for contributors! Don't be shy.** 😁 Feel free to open issues/pull requests to help me improve this project.

