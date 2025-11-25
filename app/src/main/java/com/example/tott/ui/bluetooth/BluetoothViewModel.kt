package com.example.tott.ui.bluetooth
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tott.ui.bluetooth.BluetoothListener
import com.example.tott.data.BluetoothService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// Estados posibles de la conexión
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}
class BluetoothViewModel(context: Context) : ViewModel(), BluetoothListener {

    // Servicio de Bluetooth (instanciamos pasando 'this' como listener)
    @SuppressLint("StaticFieldLeak") // Cuidado con leaks de contexto en apps grandes, aquí simplificamos
    private val bluetoothService = BluetoothService(context, this)


    // Estado de la conexión observable por la UI
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // Último mensaje recibido (ej. "Tapa abierta")
    private val _lastMessage = MutableStateFlow("")
    val lastMessage: StateFlow<String> = _lastMessage.asStateFlow()

    // Lista de dispositivos vinculados
    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices.asStateFlow()

    init {
        loadPairedDevices()
    }

    fun loadPairedDevices() {
        val devices = bluetoothService.getPairedDevices()
        if (devices != null) {
            _pairedDevices.value = devices.toList()
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        _connectionState.value = ConnectionState.CONNECTING
        bluetoothService.connectToDevice(device)
    }

    fun disconnect() {
        bluetoothService.disconnect()
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    // --- Comandos para el Basurero ---
    fun openLid() {
        bluetoothService.sendData("O")
    }

    fun closeLid() {
        bluetoothService.sendData("C")
    }

    fun setAutoMode() {
        bluetoothService.sendData("A")
    }

    fun setManualMode() {
        bluetoothService.sendData("M")
    }

    // --- Callbacks del BluetoothListener (Vienen del Service) ---

    override fun onConnected() {
        // ¡Aquí es donde sabemos que el Arduino respondió!
        viewModelScope.launch {
            _connectionState.value = ConnectionState.CONNECTED
            _lastMessage.value = "Conectado"
        }
    }

    override fun onDisconnected() {
        // Se llama si falla la conexión o si el HEARTBEAT expira (los 8 segs)
        viewModelScope.launch {
            _connectionState.value = ConnectionState.DISCONNECTED
            _lastMessage.value = "Desconectado (Sin señal)"
        }
    }

    override fun onConnectionError(error: String) {
        viewModelScope.launch {
            _connectionState.value = ConnectionState.ERROR
            _lastMessage.value = error
        }
    }

    override fun onMessageReceived(message: String) {
        viewModelScope.launch {
            // Filtramos el mensaje de latido para no llenar la pantalla,
            // pero lo usamos para saber que sigue vivo (ya lo hace el Service internamente)
            if (!message.contains("STATUS:ACTIVE")) {
                _lastMessage.value = message
            }
        }
    }
}

