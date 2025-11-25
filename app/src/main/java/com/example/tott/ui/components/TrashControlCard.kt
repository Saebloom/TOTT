package com.example.tott.ui.components

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tott.ui.bluetooth.BluetoothViewModel
import com.example.tott.ui.bluetooth.ConnectionState

@Composable
fun TrashControlCard(
    viewModel: BluetoothViewModel,
    modifier: Modifier = Modifier
) {
    // Observamos los estados del ViewModel
    val connectionState by viewModel.connectionState.collectAsState()
    val lastMessage by viewModel.lastMessage.collectAsState()
    val pairedDevices by viewModel.pairedDevices.collectAsState()

    // Colores dinámicos según estado
    val statusColor = when (connectionState) {
        ConnectionState.CONNECTED -> Color(0xFF4CAF50) // Verde
        ConnectionState.CONNECTING -> Color(0xFFFFC107) // Amarillo
        ConnectionState.DISCONNECTED -> Color(0xFFF44336) // Rojo
        ConnectionState.ERROR -> Color(0xFFB00020) // Rojo Oscuro
    }

    val statusText = when (connectionState) {
        ConnectionState.CONNECTED -> "CONECTADO"
        ConnectionState.CONNECTING -> "CONECTANDO..."
        ConnectionState.DISCONNECTED -> "DESCONECTADO"
        ConnectionState.ERROR -> "ERROR"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Encabezado de Estado ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Indicador de luz (Círculo)
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Basurero: $statusText",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Info: $lastMessage",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // --- Panel de Control (Solo visible si está conectado) ---
            if (connectionState == ConnectionState.CONNECTED) {
                Text("Control Manual", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.openLid() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("ABRIR")
                    }
                    Button(
                        onClick = { viewModel.closeLid() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("CERRAR")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { viewModel.setAutoMode() }) {
                        Text("Modo AUTO")
                    }
                    OutlinedButton(onClick = { viewModel.setManualMode() }) {
                        Text("Modo MANUAL")
                    }
                }

            } else {
                // --- Selector de Dispositivo (Si está desconectado) ---
                Text("Seleccionar Dispositivo:", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Lista simple de dispositivos (solo los primeros 3 para no ocupar mucho)
                pairedDevices.take(3).forEach { device ->
                    Button(
                        onClick = { viewModel.connectToDevice(device) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        // Intentamos obtener el nombre, si es null (permisos) ponemos la dirección
                        val name = try { device.name } catch (e: SecurityException) { device.address }
                        Text("Conectar a ${name ?: "Dispositivo"}")
                    }
                }

                if (pairedDevices.isEmpty()) {
                    Text("No hay dispositivos vinculados.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
