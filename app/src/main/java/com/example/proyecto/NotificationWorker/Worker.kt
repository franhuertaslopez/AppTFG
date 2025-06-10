package com.example.proyecto.NotificationWorker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.proyecto.R

class Worker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val startTime = prefs.getLong("notifications_start_time", 0L)
        val now = System.currentTimeMillis()

        // No enviar si han pasado menos de 5 minutos desde la activación
        if (now - startTime < 5 * 60 * 1000) {
            return Result.success()
        }

        val currentIndex = prefs.getInt("notification_index", 0)

        val title = applicationContext.getString(R.string.notification_title)
        val texts = arrayOf(
            applicationContext.getString(R.string.notification_reminder_1),
            applicationContext.getString(R.string.notification_reminder_2),
            applicationContext.getString(R.string.notification_reminder_3)
        )

        val notification = NotificationCompat.Builder(applicationContext, "default_channel")
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(texts[currentIndex])
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        NotificationManagerCompat.from(applicationContext).notify(100 + currentIndex, notification)

        val nextIndex = (currentIndex + 1) % texts.size
        prefs.edit().putInt("notification_index", nextIndex).apply()

        return Result.success()
    }
}

