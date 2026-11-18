package com.devicexpert.app.data.collector
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton class BatteryCollector @Inject constructor(@ApplicationContext private val context: Context) {
    fun getBatteryMetrics(): BatteryResult {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = context.registerReceiver(null, filter)
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val tempTenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val chargePlug = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val percentage = if (level >= 0 && scale > 0) level.toFloat() / scale.toFloat() else 0f
        val tempCelsius = if (tempTenths > 0) tempTenths / 10.0f else 0f
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        val source = when (chargePlug) { BatteryManager.BATTERY_PLUGGED_AC -> "AC"; BatteryManager.BATTERY_PLUGGED_USB -> "USB"; BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"; else -> "BATTERY" }
        return BatteryResult(percentage, tempCelsius, isCharging, source)
    }
    data class BatteryResult(val percentage: Float, val tempC: Float, val isCharging: Boolean, val source: String)
}