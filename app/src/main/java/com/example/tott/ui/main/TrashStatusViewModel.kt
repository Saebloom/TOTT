package com.example.tott.ui.main

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tott.data.BluetoothService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrashStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothService = BluetoothService(application.applicationContext)

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("--:--")
    val lastSyncTime = _lastSyncTime.asStateFlow()

    private val _fillLevel = MutableStateFlow(0)
    val fillLevel = _fillLevel.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices = _pairedDevices.asStateFlow()

    init {
        observeBluetoothState()
    }

    private fun observeBluetoothState() {
        bluetoothService.connectionState
            .onEach { state ->
                val connected = state == BluetoothService.STATE_CONNECTED
                _isConnected.value = connected
                if (connected) {
                    _lastSyncTime.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                }
            }
            .launchIn(viewModelScope)

        bluetoothService.receivedData
            .onEach { bytes ->
                // Suponiendo que el Arduino envía el nivel de llenado como un número entero en formato de texto
                bytes?.let { 
                    val receivedString = String(it)
                    try {
                        _fillLevel.value = receivedString.trim().toInt()
                    } catch (e: NumberFormatException) {
                        // Manejar el caso de que los datos recibidos no sean un número válido
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun refreshPairedDevices() {
        try {
            _pairedDevices.value = bluetoothService.getPairedDevices()?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            // Aquí se podría exponer un estado de error a la UI para solicitar permisos
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        bluetoothService.connect(device)
    }

    fun syncWithTrashCan() {
        // La UI debería llamar a refreshPairedDevices y luego permitir al usuario seleccionar un dispositivo
        // Una vez seleccionado, se llama a connectToDevice
        // Para simular, podemos intentar conectar al primer dispositivo de la lista
        val devices = bluetoothService.getPairedDevices()
        devices?.firstOrNull()?.let {
            connectToDevice(it)
        }
    }

    fun disconnect() {
        bluetoothService.stop()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothService.stop()
    }
}
