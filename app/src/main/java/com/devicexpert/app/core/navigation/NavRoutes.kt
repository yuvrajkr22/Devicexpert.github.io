package com.devicexpert.app.core.navigation
import kotlinx.serialization.Serializable
sealed interface Route {
    @Serializable data object Dashboard : Route
    @Serializable data class DeviceDetails(val deviceId: String) : Route
    @Serializable data class LiveMonitor(val deviceId: String) : Route
    @Serializable data class Analytics(val deviceId: String) : Route
    @Serializable data object Alerts : Route
    @Serializable data object Settings : Route
}