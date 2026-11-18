package com.devicexpert.app.presentation.livemonitor
import androidx.lifecycle.*
import com.devicexpert.app.data.connector.LocalAndroidDeviceConnector
import com.devicexpert.app.domain.model.device.UnifiedDevice
import com.devicexpert.app.domain.model.telemetry.TelemetrySnapshot
import com.devicexpert.app.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
data class LiveMonitorUiState(val isLoading: Boolean = true, val device: UnifiedDevice? = null, val latestSnapshot: TelemetrySnapshot? = null, val error: String? = null)
@HiltViewModel class LiveMonitorViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val repo: DeviceRepository, private val conn: LocalAndroidDeviceConnector) : ViewModel() {
    private val deviceId: String = checkNotNull(savedStateHandle["deviceId"])
    private val _uiState = MutableStateFlow(LiveMonitorUiState())
    val uiState = _uiState.asStateFlow()
    init { viewModelScope.launch {
        val device = repo.getDeviceById(deviceId)
        if (device == null) { _uiState.update { it.copy(isLoading = false, error = "Not found") }; return@launch }
        _uiState.update { it.copy(device = device, isLoading = false) }
        conn.observeLiveTelemetry(device, 1000L).catch { e -> _uiState.update { it.copy(error = e.message) } }.collect { snap -> _uiState.update { it.copy(latestSnapshot = snap) } }
    } }
}