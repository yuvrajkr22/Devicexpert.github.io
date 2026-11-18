package com.devicexpert.app.data.worker
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.devicexpert.app.DeviceXpertApp
import com.devicexpert.app.MainActivity
import com.devicexpert.app.data.local.dao.*
import com.devicexpert.app.data.local.entity.AlertEventEntity
import com.devicexpert.app.data.mapper.toDomain
import com.devicexpert.app.domain.model.alert.AlertOperator
import com.devicexpert.app.domain.model.telemetry.AndroidTelemetry
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker class AlertEvaluatorWorker @AssistedInject constructor(
    @Assisted private val context: Context, @Assisted params: WorkerParameters,
    private val alertDao: AlertDao, private val telemetryDao: TelemetryDao, private val deviceDao: DeviceDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val rules = alertDao.getActiveRules()
        if (rules.isEmpty()) return@withContext Result.success()
        val devices = deviceDao.observeAllDevices().firstOrNull() ?: return@withContext Result.success()
        for (device in devices) {
            val snapshots = telemetryDao.observeTelemetryHistory(device.id, System.currentTimeMillis() - 300000).firstOrNull()
            val latest = snapshots?.lastOrNull()?.toDomain() ?: continue
            for (rule in rules.filter { it.deviceId == null || it.deviceId == device.id }) {
                val payload = latest.payload as? AndroidTelemetry ?: continue
                val metricValue = when (rule.metricKey) {
                    "BATTERY_PCT" -> (payload.batteryPct * 100).toDouble()
                    "TEMP_C" -> payload.batteryTempC.toDouble()
                    "RAM_USED_PCT" -> (payload.ramUsedBytes.toDouble() / (8L * 1024 * 1024 * 1024)) * 100
                    else -> null
                }
                if (metricValue != null) {
                    val breached = when (AlertOperator.valueOf(rule.operator)) { AlertOperator.GT -> metricValue > rule.thresholdValue; AlertOperator.LT -> metricValue < rule.thresholdValue; AlertOperator.EQ -> metricValue == rule.thresholdValue }
                    if (breached) {
                        val event = AlertEventEntity(UUID.randomUUID().toString(), device.name, null, rule.severity, "Threshold Breached: ${device.name}", "${rule.metricKey} crossed limit. Current value: ${"%.1f".format(metricValue)}", metricValue, false, System.currentTimeMillis())
                        alertDao.insertEvent(event)
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            val intent = Intent(context, MainActivity::class.java)
                            val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                            val notif = NotificationCompat.Builder(context, DeviceXpertApp.CHANNEL_CRITICAL_ALERTS)
                                .setSmallIcon(android.R.drawable.ic_dialog_alert).setContentTitle(event.title).setContentText(event.message).setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pi).setAutoCancel(true).build()
                            NotificationManagerCompat.from(context).notify(event.alertId.hashCode(), notif)
                        }
                    }
                }
            }
        }
        Result.success()
    }
}