package com.devicexpert.app.data.local.dao
import androidx.room.*
import com.devicexpert.app.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow
@Dao interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY last_seen_millis DESC") fun observeAllDevices(): Flow<List<DeviceEntity>>
    @Query("SELECT * FROM devices WHERE id = :deviceId LIMIT 1") fun observeDevice(deviceId: String): Flow<DeviceEntity?>
    @Query("SELECT * FROM devices WHERE id = :deviceId LIMIT 1") suspend fun getDeviceById(deviceId: String): DeviceEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertOrUpdateDevice(device: DeviceEntity)
    @Query("UPDATE devices SET connection_status = :status, last_seen_millis = :lastSeen WHERE id = :deviceId") suspend fun updateConnectionStatus(deviceId: String, status: String, lastSeen: Long)
    @Query("DELETE FROM devices WHERE id = :deviceId") suspend fun deleteDevice(deviceId: String)
}