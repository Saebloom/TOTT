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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

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
                    _errorMessage.value = null // Borra el error si la conexión es exitosa
                    _lastSyncTime.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                }
            }
            .launchIn(viewModelScope)

        bluetoothService.receivedData
            .onEach { bytes ->
                val receivedString = String(bytes)
                try {
                    _fillLevel.value = receivedString.trim().toInt()
                } catch (e: NumberFormatException) {
                    _errorMessage.value = "Los datos recibidos no son válidos."
                }
            }
            .launchIn(viewModelScope)

        bluetoothService.errorMessage
            .onEach { message ->
                _errorMessage.value = message
            }
            .launchIn(viewModelScope)
    }

    fun refreshPairedDevices() {
        try {
            _pairedDevices.value = bluetoothService.getPairedDevices()?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            _errorMessage.value = "Se requiere permiso de Bluetooth."
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        bluetoothService.connect(device)
    }

    fun disconnect() {
        bluetoothService.stop()
        _isConnected.value = false
        _fillLevel.value = 0
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothService.stop()
    }
}
