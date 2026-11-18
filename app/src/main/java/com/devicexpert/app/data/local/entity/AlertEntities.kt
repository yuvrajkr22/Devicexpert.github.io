package com.devicexpert.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "alert_rules") data class AlertRuleEntity(@PrimaryKey val ruleId: String, val deviceId: String?, val metricKey: String, val operator: String, val thresholdValue: Double, val severity: String, val isEnabled: Boolean)
@Entity(tableName = "alert_events") data class AlertEventEntity(@PrimaryKey val alertId: String, val deviceId: String, val ruleId: String?, val severity: String, val title: String, val message: String, val metricValue: Double, val isAcknowledged: Boolean, val triggeredAtMillis: Long)