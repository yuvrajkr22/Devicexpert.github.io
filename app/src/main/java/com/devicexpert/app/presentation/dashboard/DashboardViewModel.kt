package com.devicexpert.app.presentation.dashboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicexpert.app.domain.model.device.*
import com.devicexpert.app.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
data class DashboardUiState(val isLoading: Boolean = true, val totalDevices: Int = 0, val onlineDevices: Int = 0, val offlineDevices: Int = 0, val devices: List<UnifiedDevice> = emptyList())
@HiltViewModel class DashboardViewModel @Inject constructor(deviceRepository: DeviceRepository) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> = deviceRepository.observeAllDevices().map { devices -> DashboardUiState(false, devices.size, devices.count { it.status == ConnectionStatus.ONLINE }, devices.count { it.status == ConnectionStatus.OFFLINE }, devices) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}