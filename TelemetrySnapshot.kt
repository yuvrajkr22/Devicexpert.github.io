package com.devicexpert.app.domain.model.telemetry
data class TelemetrySnapshot(val deviceId: String, val timestamp: Long, val healthScore: Int, val payload: TelemetryPayload)