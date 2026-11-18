package com.devicexpert.app.domain.connector
import com.devicexpert.app.domain.model.device.*
import com.devicexpert.app.domain.model.telemetry.TelemetrySnapshot
import kotlinx.coroutines.flow.Flow
interface DeviceConnector {
    val supportedCategory: DeviceCategory
    fun observeLiveTelemetry(device: UnifiedDevice, intervalMs: Long): Flow<TelemetrySnapshot>
    suspend fun fetchSnapshotOnce(device: UnifiedDevice): Result<TelemetrySnapshot>
    fun observeConnectionStatus(device: UnifiedDevice): Flow<ConnectionStatus>
}