package com.devicexpert.app.data.connector
import com.devicexpert.app.data.local.entity.TelemetryPayloadDto
import com.devicexpert.app.data.mapper.deviceJson
import com.devicexpert.app.domain.connector.DeviceConnector
import com.devicexpert.app.domain.model.device.*
import com.devicexpert.app.domain.model.telemetry.*
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.future.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class MqttIotDeviceConnector @Inject constructor() : DeviceConnector {
    override val supportedCategory: DeviceCategory = DeviceCategory.IOT
    private val client: Mqtt5AsyncClient by lazy { MqttClient.builder().useMqttVersion5().identifier(UUID.randomUUID().toString()).serverHost("broker.hivemq.com").serverPort(1883).buildAsync() }
    private var isConnected = false
    private suspend fun ensureConnected() { if (!isConnected) { client.connect().await(); isConnected = true } }
    override fun observeLiveTelemetry(device: UnifiedDevice, intervalMs: Long): Flow<TelemetrySnapshot> = callbackFlow {
        ensureConnected()
        val topic = "devicexpert/v1/user_demo/${device.id}/telemetry"
        client.subscribeWith().topicFilter(topic).callback { publish ->
            try {
                val dto = deviceJson.decodeFromString<TelemetryPayloadDto.Iot>(String(publish.payloadAsBytes, StandardCharsets.UTF_8))
                val payload = IotTelemetry(dto.temperatureC, dto.humidityPct, dto.voltageV, dto.currentMa, dto.powerMw, dto.rssiDbm, dto.uptimeSeconds, dto.customMetrics)
                var score = 100
                if (dto.temperatureC != null && dto.temperatureC > 75f) score -= 30
                if (dto.rssiDbm != null && dto.rssiDbm < -85) score -= 15
                trySend(TelemetrySnapshot(device.id, System.currentTimeMillis(), score.coerceIn(0, 100), payload))
            } catch (e: Exception) {}
        }.send()
        awaitClose { client.unsubscribeWith().topicFilter(topic).send() }
    }
    override suspend fun fetchSnapshotOnce(device: UnifiedDevice): Result<TelemetrySnapshot> = try {
        val snapshot = withTimeoutOrNull(5000L) { observeLiveTelemetry(device, 0L).firstOrNull() }
        if (snapshot != null) Result.success(snapshot) else Result.failure(Exception("Timeout"))
    } catch (e: Exception) { Result.failure(e) }
    override fun observeConnectionStatus(device: UnifiedDevice): Flow<ConnectionStatus> = callbackFlow {
        ensureConnected()
        val statusTopic = "devicexpert/v1/user_demo/${device.id}/status"
        client.subscribeWith().topicFilter(statusTopic).callback { publish ->
            val status = when (String(publish.payloadAsBytes, StandardCharsets.UTF_8).uppercase()) { "ONLINE" -> ConnectionStatus.ONLINE; "OFFLINE" -> ConnectionStatus.OFFLINE; else -> ConnectionStatus.DEGRADED }
            trySend(status)
        }.send()
        awaitClose { client.unsubscribeWith().topicFilter(statusTopic).send() }
    }
}