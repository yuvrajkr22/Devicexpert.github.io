package com.devicexpert.app.data.worker
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.devicexpert.app.data.collector.*
import com.devicexpert.app.data.local.dao.*
import com.devicexpert.app.data.mapper.*
import com.devicexpert.app.data.remote.supabase.SupabaseTelemetryDto
import com.devicexpert.app.domain.model.telemetry.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@HiltWorker class TelemetrySyncWorker @AssistedInject constructor(
    @Assisted context: Context, @Assisted params: WorkerParameters,
    private val batteryCollector: BatteryCollector, private val memoryCollector: MemoryStorageCollector,
    private val cpuCollector: CpuThermalCollector, private val deviceDao: DeviceDao,
    private val telemetryDao: TelemetryDao, private val supabaseClient: SupabaseClient
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val localDevices = deviceDao.observeAllDevices().firstOrNull()
            val localHostDevice = localDevices?.find { it.isLocalHost } ?: return@withContext Result.success()
            val battery = batteryCollector.getBatteryMetrics()
            val memory = memoryCollector.getMemoryStorageMetrics()
            val cpu = cpuCollector.getCpuThermalMetrics()
            val timestamp = System.currentTimeMillis()
            val payload = AndroidTelemetry(battery.percentage, battery.tempC, battery.isCharging, battery.source, memory.usedRamBytes, memory.usedStorageBytes, cpu.coreFreqsMhz, cpu.thermalStatus, "UNKNOWN", 0L, 0L, android.os.SystemClock.elapsedRealtime(), emptyMap())
            var score = 100
            if (battery.tempC > 40f) score -= 20
            if (battery.tempC > 45f) score -= 30
            val ramPct = if (memory.totalRamBytes > 0) memory.usedRamBytes.toFloat() / memory.totalRamBytes else 0f
            if (ramPct > 0.85f) score -= 15
            val snapshot = TelemetrySnapshot(localHostDevice.id, timestamp, score.coerceIn(0, 100), payload)
            telemetryDao.insertSnapshot(snapshot.toEntity())
            deviceDao.updateConnectionStatus(localHostDevice.id, "ONLINE", timestamp)
            val session = supabaseClient.auth.currentSessionOrNull()
            if (session != null) {
                val isoTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
                supabaseClient.postgrest["telemetry_logs"].insert(SupabaseTelemetryDto(localHostDevice.id, isoTime, snapshot.healthScore, deviceJson.decodeFromString<JsonElement>(snapshot.toEntity().payloadJson)))
            }
            Result.success()
        } catch (e: Exception) { Result.retry() }
    }
}