package com.devicexpert.app.data.local.entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable sealed interface DeviceMetadataDto {
    @Serializable @SerialName("ANDROID") data class Android(val manufacturer: String, val model: String, val androidVersion: String, val sdkInt: Int, val socModel: String, val totalRamBytes: Long, val totalStorageBytes: Long, val sensorCount: Int) : DeviceMetadataDto
    @Serializable @SerialName("COMPUTER") data class Computer(val hostname: String, val osName: String, val osVersion: String, val cpuModel: String, val coreCount: Int, val totalRamBytes: Long, val totalDiskBytes: Long, val agentVersion: String) : DeviceMetadataDto
    @Serializable @SerialName("IOT") data class Iot(val boardModel: String, val firmwareVersion: String, val macAddress: String, val ipAddress: String, val mqttTopicPrefix: String, val capabilities: List<String>) : DeviceMetadataDto
}

@Serializable sealed interface TelemetryPayloadDto {
    @Serializable @SerialName("ANDROID") data class Android(val batteryPct: Float, val batteryTempC: Float, val isCharging: Boolean, val chargingSource: String, val ramUsedBytes: Long, val storageUsedBytes: Long, val cpuCoreFreqsMhz: List<Int>, val thermalStatus: Int, val netType: String, val rxBytesPerSec: Long, val txBytesPerSec: Long, val uptimeMillis: Long, val activeSensorReadings: Map<String, Float>) : TelemetryPayloadDto
    @Serializable @SerialName("COMPUTER") data class Computer(val cpuUsagePct: Float, val ramUsedBytes: Long, val diskUsedBytes: Long, val netRxBps: Long, val netTxBps: Long, val cpuTempC: Float?, val uptimeSeconds: Long, val topProcessCount: Int) : TelemetryPayloadDto
    @Serializable @SerialName("IOT") data class Iot(val temperatureC: Float?, val humidityPct: Float?, val voltageV: Float?, val currentMa: Float?, val powerMw: Float?, val rssiDbm: Int?, val uptimeSeconds: Long, val customMetrics: Map<String, Double>) : TelemetryPayloadDto
}