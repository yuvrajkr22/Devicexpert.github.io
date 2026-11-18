package com.devicexpert.app.presentation.alerts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devicexpert.app.core.designsystem.*
import com.devicexpert.app.core.designsystem.components.TelemetryCard
import com.devicexpert.app.data.local.dao.AlertDao
import com.devicexpert.app.data.local.entity.AlertEventEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class AlertsViewModel @Inject constructor(private val alertDao: AlertDao) : ViewModel() {
    val unacknowledgedEvents = alertDao.observeUnacknowledgedEvents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun acknowledge(alertId: String) = viewModelScope.launch { alertDao.acknowledgeEvent(alertId) }
}
@OptIn(ExperimentalMaterial3Api::class) @Composable fun AlertsScreen(viewModel: AlertsViewModel = hiltViewModel()) {
    val events by viewModel.unacknowledgedEvents.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Active Alerts") }) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(events, key = { it.alertId }) { event ->
                val color = if (event.severity == "CRITICAL") StatusCriticalCrimson else StatusWarningAmber
                TelemetryCard(Modifier.fillMaxWidth()) { Text(event.title, style = MaterialTheme.typography.titleMedium, color = color); Spacer(Modifier.height(4.dp)); Text(event.message, style = MaterialTheme.typography.bodyMedium); Spacer(Modifier.height(12.dp)); Button(onClick = { viewModel.acknowledge(event.alertId) }, colors = ButtonDefaults.buttonColors(containerColor = color)) { Text("Acknowledge") } }
            }
        }
    }
}