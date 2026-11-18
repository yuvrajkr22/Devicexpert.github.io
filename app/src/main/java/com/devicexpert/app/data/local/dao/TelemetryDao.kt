package com.devicexpert.app.data.local.dao
import androidx.room.*
import com.devicexpert.app.data.local.entity.TelemetryEntity
import kotlinx.coroutines.flow.Flow
@Dao interface TelemetryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertSnapshot(snapshot: TelemetryEntity)
    @Query("SELECT * FROM telemetry_snapshots WHERE device_id = :deviceId AND timestamp >= :sinceMillis ORDER BY timestamp ASC") fun observeTelemetryHistory(deviceId: String, sinceMillis: Long): Flow<List<TelemetryEntity>>
    @Query("DELETE FROM telemetry_snapshots WHERE timestamp < :beforeMillis") suspend fun pruneOldSnapshots(beforeMillis: Long): Int
}