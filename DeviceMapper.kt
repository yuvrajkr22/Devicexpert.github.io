package com.devicexpert.app.data.mapper
import com.devicexpert.app.data.local.entity.*
import com.devicexpert.app.domain.model.device.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
val deviceJson = Json { ignoreUnknownKeys = true; classDiscriminator = "type" }
fun DeviceEntity.toDomain(): UnifiedDevice {
    val metadataDto = deviceJson.decodeFromString<DeviceMetadataDto>(this.metadataJson)
    val metadataDomain = when (metadataDto) {
        is DeviceMetadataDto.Android -> AndroidMetadata(metadataDto.manufacturer, metadataDto.model, metadataDto.androidVersion, metadataDto.sdkInt, metadataDto.socModel, metadataDto.totalRamBytes, metadataDto.totalStorageBytes, metadataDto.sensorCount)
        is DeviceMetadataDto.Computer -> ComputerMetadata(metadataDto.hostname, metadataDto.osName, metadataDto.osVersion, metadataDto.cpuModel, metadataDto.coreCount, metadataDto.totalRamBytes, metadataDto.totalDiskBytes, metadataDto.agentVersion)
        is DeviceMetadataDto.Iot -> IotMetadata(metadataDto.boardModel, metadataDto.firmwareVersion, metadataDto.macAddress, metadataDto.ipAddress, metadataDto.mqttTopicPrefix, metadataDto.capabilities)
    }
    return UnifiedDevice(this.id, this.name, this.category, this.connectionStatus, this.isLocalHost, this.lastSeenMillis, metadataDomain, null)
}
fun UnifiedDevice.toEntity(): DeviceEntity {
    val metadataDto = when (val meta = this.metadata) {
        is AndroidMetadata -> DeviceMetadataDto.Android(meta.manufacturer, meta.model, meta.androidVersion, meta.sdkInt, meta.socModel, meta.totalRamBytes, meta.totalStorageBytes, meta.sensorCount)
        is ComputerMetadata -> DeviceMetadataDto.Computer(meta.hostname, meta.osName, meta.osVersion, meta.cpuModel, meta.coreCount, meta.totalRamBytes, meta.totalDiskBytes, meta.agentVersion)
        is IotMetadata -> DeviceMetadataDto.Iot(meta.boardModel, meta.firmwareVersion, meta.macAddress, meta.ipAddress, meta.mqttTopicPrefix, meta.capabilities)
    }
    return DeviceEntity(this.id, this.name, this.category, this.status, this.isLocalHost, this.lastSeenMillis, deviceJson.encodeToString(metadataDto))
}