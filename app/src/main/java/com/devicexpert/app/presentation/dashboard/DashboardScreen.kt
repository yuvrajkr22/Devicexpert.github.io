package com.devicexpert.app.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToLiveMonitor: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var deviceName by remember { mutableStateOf("My Android Phone") }
    var isRegistering by remember { mutableStateOf(false) }
    var registrationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("DeviceXpert", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    registrationError = null
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Device")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TelemetryCard(Modifier.weight(1f)) {
                        Text(
                            "ONLINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusHealthyEmerald
                        )
                        Text(
                            "${uiState.onlineDevices}",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    TelemetryCard(Modifier.weight(1f)) {
                        Text(
                            "OFFLINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusOfflineSlate
                        )
                        Text(
                            "${uiState.offlineDevices}",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }
            }

            item {
                Text(
                    "Registered Hardware",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uiState.devices, key = { it.id }) { device ->
                TelemetryCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToLiveMonitor(device.id)
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                device.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                device.category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            device.status.name,
                            color = if (device.status == ConnectionStatus.ONLINE)
                                StatusHealthyEmerald
                            else StatusOfflineSlate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isRegistering) showAddDialog = false
            },
            title = {
                Text("Add Android Device")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Register this Android phone with DeviceXpert.")

                    OutlinedTextField(
                        value = deviceName,
                        onValueChange = {
                            deviceName = it
                            registrationError = null
                        },
                        label = { Text("Device name") },
                        singleLine = true,
                        enabled = !isRegistering
                    )

                    registrationError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isRegistering && deviceName.isNotBlank(),
                    onClick = {
                        isRegistering = true
                        registrationError = null

                        viewModel.registerCurrentAndroidDevice(deviceName) { result ->
                            isRegistering = false

                            result.fold(
                                onSuccess = { deviceId ->
                                    showAddDialog = false
                                    onNavigateToLiveMonitor(deviceId)
                                },
                                onFailure = { error ->
                                    registrationError =
                                        error.message ?: "Registration failed"
                                }
                            )
                        }
                    }
                ) {
                    if (isRegistering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Register Device")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isRegistering,
                    onClick = {
                        showAddDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
