package com.devicexpert.app.data.remote.supabase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
@Serializable data class SupabaseTelemetryDto(@SerialName("device_id") val deviceId: String, @SerialName("recorded_at") val recordedAtIso: String, @SerialName("health_score") val healthScore: Int, @SerialName("payload") val payload: JsonElement)