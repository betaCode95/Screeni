package com.specimen.modular.plinth.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.specimen.modular.plinth.R
import com.specimen.modular.plinth.components.PlinthActivity
import com.specimen.modular.plinth.components.PlinthService

fun PlinthActivity<*>.screenDensity() = this.resources.displayMetrics.density.toInt()

fun PlinthService.screenDensity() = this.resources.displayMetrics.density.toInt()

fun notification(context: Context): Notification? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("loc",
            "Plinth",
            NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(context, "loc")
            .setContentTitle("Screen Capture")
            .setContentText("Capturing Screen Service..")
            .setSmallIcon(R.drawable.ic_back)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        return builder.build()
    } else {
        val builder = NotificationCompat.Builder(context, "loc")
            .setContentTitle("Syncing Locations")
            .setContentText("Capturing Screen Service..")
            .setSmallIcon(R.drawable.ic_back)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        return builder.build()
    }
}