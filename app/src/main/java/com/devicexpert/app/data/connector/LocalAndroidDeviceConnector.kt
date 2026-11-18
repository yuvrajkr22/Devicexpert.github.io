package com.devicexpert.app.data.connector
import com.devicexpert.app.data.collector.*
import com.devicexpert.app.domain.connector.DeviceConnector
import com.devicexpert.app.domain.model.device.*
import com.devicexpert.app.domain.model.telemetry.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class LocalAndroidDeviceConnector @Inject constructor(
    private val batteryCollector: BatteryCollector, private val memoryCollector: MemoryStorageCollector, private val cpuCollector: CpuThermalCollector
) : DeviceConnector {
    override val supportedCategory: DeviceCategory = DeviceCategory.ANDROID
    override fun observeLiveTelemetry(device: UnifiedDevice, intervalMs: Long): Flow<TelemetrySnapshot> = flow {
        while (true) { emit(fetchSnapshotOnce(device).getOrThrow()); delay(intervalMs) }
    }
    override suspend fun fetchSnapshotOnce(device: UnifiedDevice): Result<TelemetrySnapshot> = try {
        val battery = batteryCollector.getBatteryMetrics()
        val memory = memoryCollector.getMemoryStorageMetrics()
        val cpu = cpuCollector.getCpuThermalMetrics()
        val payload = AndroidTelemetry(battery.percentage, battery.tempC, battery.isCharging, battery.source, memory.usedRamBytes, memory.usedStorageBytes, cpu.coreFreqsMhz, cpu.thermalStatus, "UNKNOWN", 0L, 0L, android.os.SystemClock.elapsedRealtime(), emptyMap())
        var score = 100
        if (battery.tempC > 40f) score -= 20
        val ramUsage = if (memory.totalRamBytes > 0) memory.usedRamBytes.toFloat() / memory.totalRamBytes else 0f
        if (ramUsage > 0.85f) score -= 15
        Result.success(TelemetrySnapshot(device.id, System.currentTimeMillis(), score.coerceIn(0, 100), payload))
    } catch (e: Exception) { Result.failure(e) }
    override fun observeConnectionStatus(device: UnifiedDevice): Flow<ConnectionStatus> = flow { emit(ConnectionStatus.ONLINE) }
}