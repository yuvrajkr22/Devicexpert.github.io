package com.devicexpert.app.domain.model.device
import com.devicexpert.app.domain.model.telemetry.TelemetrySnapshot
data class UnifiedDevice(val id: String, val name: String, val category: DeviceCategory, val status: ConnectionStatus, val isLocalHost: Boolean, val lastSeenMillis: Long, val metadata: DeviceMetadata, val latestTelemetry: TelemetrySnapshot? = null)