package com.devicexpert.app.domain.model.alert
enum class AlertSeverity { INFO, WARNING, CRITICAL }
enum class AlertOperator { GT, LT, EQ }
data class AlertRule(val ruleId: String, val deviceId: String?, val metricKey: String, val operator: AlertOperator, val thresholdValue: Double, val severity: AlertSeverity, val isEnabled: Boolean)
data class AlertEvent(val alertId: String, val deviceId: String, val ruleId: String?, val severity: AlertSeverity, val title: String, val message: String, val metricValue: Double, val isAcknowledged: Boolean, val triggeredAtMillis: Long)