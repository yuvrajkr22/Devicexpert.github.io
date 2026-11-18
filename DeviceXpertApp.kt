package com.devicexpert.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DeviceXpertApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService<NotificationManager>() ?: return
        val criticalAlertsChannel = NotificationChannel(
            CHANNEL_CRITICAL_ALERTS, "Critical Device Alerts", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "High-priority alerts for thermal, battery, and offline device events"; enableVibration(true) }
        val backgroundSyncChannel = NotificationChannel(
            CHANNEL_BACKGROUND_MONITOR, "Background Telemetry Service", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Status notifications for continuous telemetry monitoring"; setShowBadge(false) }
        notificationManager.createNotificationChannels(listOf(criticalAlertsChannel, backgroundSyncChannel))
    }

    companion object {
        const val CHANNEL_CRITICAL_ALERTS = "devicexpert_critical_alerts"
        const val CHANNEL_BACKGROUND_MONITOR = "devicexpert_background_monitor"
    }
}