package com.devicexpert.app.presentation.analytics
import androidx.lifecycle.*
import com.devicexpert.app.data.local.dao.TelemetryDao
import com.devicexpert.app.data.mapper.toDomain
import com.devicexpert.app.domain.model.telemetry.AndroidTelemetry
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel class AnalyticsViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val telemetryDao: TelemetryDao) : ViewModel() {
    private val deviceId: String = checkNotNull(savedStateHandle["deviceId"])
    val batteryChartProducer = CartesianChartModelProducer()
    val tempChartProducer = CartesianChartModelProducer()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    init { viewModelScope.launch(Dispatchers.IO) {
        telemetryDao.observeTelemetryHistory(deviceId, System.currentTimeMillis() - 86400000).catch {}.collect { entities ->
            val batteryPoints = mutableListOf<Float>(); val tempPoints = mutableListOf<Float>()
            entities.map { it.toDomain() }.forEach { snap -> val pl = snap.payload as? AndroidTelemetry; if (pl != null) { batteryPoints.add(pl.batteryPct * 100f); tempPoints.add(pl.batteryTempC) } }
            if (batteryPoints.isNotEmpty()) { batteryChartProducer.runTransaction { lineSeries { series(batteryPoints) } }; tempChartProducer.runTransaction { lineSeries { series(tempPoints) } } }
            _isLoading.update { false }
        }
    } }
}