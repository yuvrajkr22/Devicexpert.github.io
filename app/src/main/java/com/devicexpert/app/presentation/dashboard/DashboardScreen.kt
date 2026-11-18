package com.devicexpert.app.presentation.dashboard
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devicexpert.app.core.designsystem.*
import com.devicexpert.app.core.designsystem.components.TelemetryCard
import com.devicexpert.app.domain.model.device.*

@OptIn(ExperimentalMaterial3Api::class) @Composable fun DashboardScreen(onNavigateToLiveMonitor: (String) -> Unit, viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("DeviceXpert", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }, floatingActionButton = { FloatingActionButton(onClick = { }) { Icon(Icons.Default.Add, contentDescription = "Add") } }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) { TelemetryCard(modifier = Modifier.weight(1f)) { Text("ONLINE", style = MaterialTheme.typography.labelSmall, color = StatusHealthyEmerald); Text("${uiState.onlineDevices}", style = MaterialTheme.typography.headlineLarge) }; TelemetryCard(modifier = Modifier.weight(1f)) { Text("OFFLINE", style = MaterialTheme.typography.labelSmall, color = StatusOfflineSlate); Text("${uiState.offlineDevices}", style = MaterialTheme.typography.headlineLarge) } } }
            item { Text("Registered Hardware", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp)) }
            items(uiState.devices, key = { it.id }) { device -> TelemetryCard(modifier = Modifier.fillMaxWidth().clickable { onNavigateToLiveMonitor(device.id) }) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Column { Text(device.name, style = MaterialTheme.typography.titleMedium); Text(device.category.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }; Text(device.status.name, color = if (device.status == ConnectionStatus.ONLINE) StatusHealthyEmerald else StatusOfflineSlate, fontSize = 12.sp, fontWeight = FontWeight.Bold) } } }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}