package com.devicexpert.app.data.mapper
import com.devicexpert.app.data.local.entity.*
import com.devicexpert.app.domain.model.telemetry.*
import kotlinx.serialization.encodeToString
fun TelemetryEntity.toDomain(): TelemetrySnapshot {
    val payloadDto = deviceJson.decodeFromString<TelemetryPayloadDto>(this.payloadJson)
    val payloadDomain = when (payloadDto) {
        is TelemetryPayloadDto.Android -> AndroidTelemetry(payloadDto.batteryPct, payloadDto.batteryTempC, payloadDto.isCharging, payloadDto.chargingSource, payloadDto.ramUsedBytes, payloadDto.storageUsedBytes, payloadDto.cpuCoreFreqsMhz, payloadDto.thermalStatus, payloadDto.netType, payloadDto.rxBytesPerSec, payloadDto.txBytesPerSec, payloadDto.uptimeMillis, payloadDto.activeSensorReadings)
        is TelemetryPayloadDto.Computer -> ComputerTelemetry(payloadDto.cpuUsagePct, payloadDto.ramUsedBytes, payloadDto.diskUsedBytes, payloadDto.netRxBps, payloadDto.netTxBps, payloadDto.cpuTempC, payloadDto.uptimeSeconds, payloadDto.topProcessCount)
        is TelemetryPayloadDto.Iot -> IotTelemetry(payloadDto.temperatureC, payloadDto.humidityPct, payloadDto.voltageV, payloadDto.currentMa, payloadDto.powerMw, payloadDto.rssiDbm, payloadDto.uptimeSeconds, payloadDto.customMetrics)
    }
    return TelemetrySnapshot(this.deviceId, this.timestamp, this.healthScore, payloadDomain)
}
fun TelemetrySnapshot.toEntity(): TelemetryEntity {
    val payloadDto = when (val pl = this.payload) {
        is AndroidTelemetry -> TelemetryPayloadDto.Android(pl.batteryPct, pl.batteryTempC, pl.isCharging, pl.chargingSource, pl.ramUsedBytes, pl.storageUsedBytes, pl.cpuCoreFreqsMhz, pl.thermalStatus, pl.netType, pl.rxBytesPerSec, pl.txBytesPerSec, pl.uptimeMillis, pl.activeSensorReadings)
        is ComputerTelemetry -> TelemetryPayloadDto.Computer(pl.cpuUsagePct, pl.ramUsedBytes, pl.diskUsedBytes, pl.netRxBps, pl.netTxBps, pl.cpuTempC, pl.uptimeSeconds, pl.topProcessCount)
        is IotTelemetry -> TelemetryPayloadDto.Iot(pl.temperatureC, pl.humidityPct, pl.voltageV, pl.currentMa, pl.powerMw, pl.rssiDbm, pl.uptimeSeconds, pl.customMetrics)
    }
    return TelemetryEntity(deviceId = this.deviceId, timestamp = this.timestamp, healthScore = this.healthScore, payloadJson = deviceJson.encodeToString(payloadDto))
}