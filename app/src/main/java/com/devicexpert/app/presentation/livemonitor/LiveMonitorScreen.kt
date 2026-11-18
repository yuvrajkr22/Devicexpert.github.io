package com.devicexpert.app.presentation.livemonitor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devicexpert.app.core.designsystem.AppTypography
import com.devicexpert.app.core.designsystem.components.*
import com.devicexpert.app.domain.model.telemetry.AndroidTelemetry

@OptIn(ExperimentalMaterial3Api::class) @Composable fun LiveMonitorScreen(onNavigateBack: () -> Unit, viewModel: LiveMonitorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("Live Monitor") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else if (uiState.error != null) Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            else {
                val payload = uiState.latestSnapshot?.payload as? AndroidTelemetry
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(uiState.device?.name ?: "Unknown", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            val ramPct = if (payload != null && payload.storageUsedBytes > 0) payload.ramUsedBytes.toFloat() / (8L*1024*1024*1024) else 0f
                            TelemetryCard(modifier = Modifier.fillMaxWidth()) { Text("RAM Load", style = MaterialTheme.typography.titleSmall); Spacer(Modifier.height(16.dp)); MetricGauge(ramPct, Modifier.fillMaxWidth().aspectRatio(1f)) }
                        }
                        item {
                            TelemetryCard(modifier = Modifier.fillMaxWidth()) { Text("Battery", style = MaterialTheme.typography.titleSmall); Spacer(Modifier.height(16.dp)); MetricGauge(payload?.batteryPct ?: 0f, Modifier.fillMaxWidth().aspectRatio(1f)); Spacer(Modifier.height(8.dp)); Text("${payload?.batteryTempC ?: 0f}°C", style = AppTypography.labelLarge, modifier = Modifier.align(Alignment.CenterHorizontally)) }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TelemetryCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Detailed Specifications", style = MaterialTheme.typography.titleMedium); Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth().padding(vertical=4.dp), horizontalArrangement=Arrangement.SpaceBetween) { Text("CPU Cores", style=MaterialTheme.typography.bodyMedium); Text(payload?.cpuCoreFreqsMhz?.joinToString(", ") { "${it}Mhz" } ?: "N/A", style=AppTypography.labelLarge) }
                        Row(Modifier.fillMaxWidth().padding(vertical=4.dp), horizontalArrangement=Arrangement.SpaceBetween) { Text("Uptime", style=MaterialTheme.typography.bodyMedium); Text("${(payload?.uptimeMillis ?: 0)/60000} mins", style=AppTypography.labelLarge) }
                    }
                }
            }
        }
    }
}