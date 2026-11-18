package com.devicexpert.app.data.local.dao
import androidx.room.*
import com.devicexpert.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow
@Dao interface AlertDao {
    @Query("SELECT * FROM alert_rules WHERE isEnabled = 1") suspend fun getActiveRules(): List<AlertRuleEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertRule(rule: AlertRuleEntity)
    @Query("SELECT * FROM alert_events ORDER BY triggeredAtMillis DESC") fun observeAllEvents(): Flow<List<AlertEventEntity>>
    @Query("SELECT * FROM alert_events WHERE isAcknowledged = 0 ORDER BY triggeredAtMillis DESC") fun observeUnacknowledgedEvents(): Flow<List<AlertEventEntity>>
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertEvent(event: AlertEventEntity)
    @Query("UPDATE alert_events SET isAcknowledged = 1 WHERE alertId = :alertId") suspend fun acknowledgeEvent(alertId: String)
}