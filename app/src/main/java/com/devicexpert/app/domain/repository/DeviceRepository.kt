package com.devicexpert.app.domain.repository
import com.devicexpert.app.domain.model.device.*
import kotlinx.coroutines.flow.Flow
interface DeviceRepository {
    fun observeAllDevices(): Flow<List<UnifiedDevice>>
    fun observeDevice(deviceId: String): Flow<UnifiedDevice?>
    suspend fun getDeviceById(deviceId: String): UnifiedDevice?
    suspend fun registerDevice(device: UnifiedDevice): Result<Unit>
    suspend fun updateConnectionStatus(deviceId: String, status: ConnectionStatus, lastSeen: Long): Result<Unit>
    suspend fun removeDevice(deviceId: String): Result<Unit>
}