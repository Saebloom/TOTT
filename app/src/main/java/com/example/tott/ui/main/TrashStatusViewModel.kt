package com.example.tott.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrashStatusViewModel : ViewModel() {

    // Flujo para el estado de la conexión
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    // Flujo para el nivel de batería (siempre 100%)
    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel = _batteryLevel.asStateFlow()

    // Flujo para la última sincronización
    private val _lastSyncTime = MutableStateFlow("--:--")
    val lastSyncTime = _lastSyncTime.asStateFlow()

    // Flujo para el nivel de llenado del basurero
    private val _fillLevel = MutableStateFlow(0)
    val fillLevel = _fillLevel.asStateFlow()

    // TODO: Inyectar y usar el BluetoothService aquí

    fun syncWithTrashCan() {
        // Aquí irá la lógica para escanear y conectar con el Arduino
        // Por ahora, simulamos una conexión exitosa
        _isConnected.value = true
        _fillLevel.value = (30..70).random() // Simula un nivel de llenado aleatorio
        _lastSyncTime.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun disconnect() {
        _isConnected.value = false
    }
}
