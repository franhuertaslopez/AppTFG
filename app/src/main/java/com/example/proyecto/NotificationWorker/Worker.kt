package com.example.proyecto.NotificationWorker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.proyecto.R

class Worker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val CHANNEL_ID = "default_channel"

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {
        val prefs = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "en") ?: "en"

        val localizedContext = updateLocale(applicationContext, language)

        val notificationManager = localizedContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(localizedContext, CHANNEL_ID)
            .setContentTitle(localizedContext.getString(R.string.notification_1))
            .setContentText(localizedContext.getString(R.string.notification_2))
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun updateLocale(context: Context, language: String): Context {
        val locale = java.util.Locale(language)
        java.util.Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

}
