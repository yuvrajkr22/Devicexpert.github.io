package com.devicexpert.app.data.collector
import android.content.Context
import android.os.Build
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
@Singleton class CpuThermalCollector @Inject constructor(@ApplicationContext private val context: Context) {
    fun getCpuThermalMetrics(): CpuThermalResult {
        val freqsMhz = mutableListOf<Int>()
        val numCores = Runtime.getRuntime().availableProcessors()
        for (i in 0 until numCores) {
            try {
                val freqFile = File("/sys/devices/system/cpu/cpu$i/cpufreq/scaling_cur_freq")
                if (freqFile.exists() && freqFile.canRead()) {
                    val freqKhz = freqFile.readText().trim().toIntOrNull() ?: 0
                    freqsMhz.add(freqKhz / 1000)
                } else { freqsMhz.add(0) }
            } catch (e: Exception) { freqsMhz.add(0) }
        }
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val thermalStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) powerManager.currentThermalStatus else 0
        return CpuThermalResult(freqsMhz, thermalStatus)
    }
    data class CpuThermalResult(val coreFreqsMhz: List<Int>, val thermalStatus: Int)
}