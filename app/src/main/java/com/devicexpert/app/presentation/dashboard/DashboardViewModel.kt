package com.devicexpert.app.presentation.dashboard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicexpert.app.data.collector.MemoryStorageCollector
import com.devicexpert.app.domain.model.device.*
import com.devicexpert.app.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalDevices: Int = 0,
    val onlineDevices: Int = 0,
    val offlineDevices: Int = 0,
    val devices: List<UnifiedDevice> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val memoryCollector: MemoryStorageCollector,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        deviceRepository.observeAllDevices()
            .map { devices ->
                DashboardUiState(
                    isLoading = false,
                    totalDevices = devices.size,
                    onlineDevices = devices.count {
                        it.status == ConnectionStatus.ONLINE
                    },
                    offlineDevices = devices.count {
                        it.status == ConnectionStatus.OFFLINE
                    },
                    devices = devices
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DashboardUiState()
            )

    fun registerCurrentAndroidDevice(
        deviceName: String,
        onComplete: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val androidId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                ) ?: "unknown-android"

                val deviceId = "android-$androidId"

                val memory = memoryCollector.getMemoryStorageMetrics()

                val sensorManager = context.getSystemService(
                    Context.SENSOR_SERVICE
                ) as SensorManager

                val socModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Build.SOC_MODEL ?: "Unknown"
                } else {
                    "Unknown"
                }

                val metadata = AndroidMetadata(
                    manufacturer = Build.MANUFACTURER ?: "Unknown",
                    model = Build.MODEL ?: "Unknown",
                    androidVersion = Build.VERSION.RELEASE ?: "Unknown",
                    sdkInt = Build.VERSION.SDK_INT,
                    socModel = socModel,
                    totalRamBytes = memory.totalRamBytes,
                    totalStorageBytes = memory.totalStorageBytes,
                    sensorCount = sensorManager.getSensorList(Sensor.TYPE_ALL).size
                )

                val device = UnifiedDevice(
                    id = deviceId,
                    name = deviceName.trim(),
                    category = DeviceCategory.ANDROID,
                    status = ConnectionStatus.ONLINE,
                    isLocalHost = true,
                    lastSeenMillis = System.currentTimeMillis(),
                    metadata = metadata
                )

                val result = deviceRepository.registerDevice(device)

                onComplete(result.map { deviceId })
            } catch (e: Exception) {
                onComplete(Result.failure(e))
            }
        }
    }
}
