package com.devicexpert.app.data.repository
import com.devicexpert.app.data.local.dao.DeviceDao
import com.devicexpert.app.data.mapper.*
import com.devicexpert.app.domain.model.device.*
import com.devicexpert.app.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class DeviceRepositoryImpl @Inject constructor(private val deviceDao: DeviceDao) : DeviceRepository {
    override fun observeAllDevices(): Flow<List<UnifiedDevice>> = deviceDao.observeAllDevices().map { list -> list.map { it.toDomain() } }
    override fun observeDevice(deviceId: String): Flow<UnifiedDevice?> = deviceDao.observeDevice(deviceId).map { it?.toDomain() }
    override suspend fun getDeviceById(deviceId: String): UnifiedDevice? = deviceDao.getDeviceById(deviceId)?.toDomain()
    override suspend fun registerDevice(device: UnifiedDevice): Result<Unit> = try { deviceDao.insertOrUpdateDevice(device.toEntity()); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun updateConnectionStatus(deviceId: String, status: ConnectionStatus, lastSeen: Long): Result<Unit> = try { deviceDao.updateConnectionStatus(deviceId, status.name, lastSeen); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    override suspend fun removeDevice(deviceId: String): Result<Unit> = try { deviceDao.deleteDevice(deviceId); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
}