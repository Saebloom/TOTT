package com.example.tott.ui.main

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tott.R
import com.example.tott.ui.theme.TOTTTheme

@Composable
fun TrashStatusView(viewModel: TrashStatusViewModel = viewModel()) {
    val isConnected by viewModel.isConnected.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val lastSync by viewModel.lastSyncTime.collectAsState()
    val fillLevel by viewModel.fillLevel.collectAsState()
    val pairedDevices by viewModel.pairedDevices.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDeviceDialog by remember { mutableStateOf(false) }

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.values.all { it }) {
                viewModel.refreshPairedDevices()
                showDeviceDialog = true
            } else {
                viewModel.clearError() // Opcional: o mostrar un error específico de permisos
            }
        }
    )

    if (showDeviceDialog) {
        DeviceSelectionDialog(
            devices = pairedDevices,
            onDeviceSelected = {
                viewModel.connectToDevice(it)
                showDeviceDialog = false
            },
            onDismiss = { showDeviceDialog = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatusChip(isConnected = isConnected)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isConnected) {
                    ConnectedView(batteryLevel, lastSync, fillLevel)
                } else {
                    DisconnectedView(errorMessage)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (isConnected) {
                        viewModel.disconnect()
                    } else {
                        permissionLauncher.launch(permissionsToRequest)
                    }
                }) {
                    Text(if (isConnected) "Desconectar" else "Sincronizar")
                }
            }
        }

        // ... (el resto de la UI como la tarjeta de 'Última vez vaciado')
    }
}

@Composable
private fun ConnectedView(batteryLevel: Int, lastSync: String, fillLevel: Int) {
    Image(
        painter = painterResource(id = R.drawable.basurero),
        contentDescription = "Imagen del basurero",
        modifier = Modifier.height(150.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text("Nivel de batería: $batteryLevel%", style = MaterialTheme.typography.bodyMedium)
    Text("Última sincronización: $lastSync", style = MaterialTheme.typography.bodyMedium)
    Text("Nivel de llenado: $fillLevel%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
}

@Composable
private fun DisconnectedView(errorMessage: String?) {
    Icon(
        imageVector = Icons.Filled.Warning,
        contentDescription = "Desconectado",
        modifier = Modifier.size(60.dp),
        tint = MaterialTheme.colorScheme.outline
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = errorMessage ?: "Basurero desconectado",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        color = if (errorMessage != null) MaterialTheme.colorScheme.error else LocalContentColor.current
    )
    Text(
        text = "Pulsa \"Sincronizar\" para buscar y conectar el dispositivo.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun DeviceSelectionDialog(
    devices: List<BluetoothDevice>,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Seleccionar Dispositivo", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                if (devices.isEmpty()) {
                    Text("No se encontraron dispositivos vinculados. Asegúrate de vincular tu dispositivo desde los ajustes de Bluetooth de tu teléfono.")
                } else {
                    LazyColumn {
                        items(devices) { device ->
                            try {
                                Text(
                                    text = device.name ?: "Dispositivo desconocido",
                                    modifier = Modifier.fillMaxWidth().clickable { onDeviceSelected(device) }.padding(vertical = 12.dp)
                                )
                            } catch(e: SecurityException) {
                                // El permiso ya debería estar concedido aquí
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(isConnected: Boolean) {
    val chipText = if (isConnected) "Conectado" else "Desconectado"
    val icon = if (isConnected) Icons.Filled.CheckCircle else Icons.Filled.Warning
    val containerColor = if (isConnected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
    val contentColor = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = "Estado", tint = contentColor, modifier = Modifier.size(18.dp))
            Text(text = chipText, color = contentColor, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TOTTTheme {
        TrashStatusView()
    }
}
