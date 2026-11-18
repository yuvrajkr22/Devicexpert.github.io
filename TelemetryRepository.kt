package com.devicexpert.app.domain.repository
import com.devicexpert.app.domain.model.telemetry.TelemetrySnapshot
import kotlinx.coroutines.flow.Flow
interface TelemetryRepository {
    fun observeTelemetryHistory(deviceId: String, sinceMillis: Long): Flow<List<TelemetrySnapshot>>
    suspend fun saveSnapshot(snapshot: TelemetrySnapshot): Result<Unit>
    suspend fun pruneOldSnapshots(beforeMillis: Long): Result<Int>
}