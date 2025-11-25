package com.example.tott.data

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothService(private val context: Context) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null

    private val _connectionState = MutableStateFlow(STATE_NONE)
    val connectionState: StateFlow<Int> = _connectionState.asStateFlow()

    private val _receivedData = MutableSharedFlow<ByteArray>()
    val receivedData: SharedFlow<ByteArray> = _receivedData.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    companion object {
        const val STATE_NONE = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @Throws(SecurityException::class)
    fun getPairedDevices(): Set<BluetoothDevice>? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("BLUETOOTH_CONNECT permission not granted")
        }
        return bluetoothAdapter?.bondedDevices
    }

    @Synchronized
    fun connect(device: BluetoothDevice) {
        if (_connectionState.value == STATE_CONNECTING) {
            connectThread?.cancel()
            connectThread = null
        }

        connectedThread?.cancel()
        connectedThread = null

        connectThread = ConnectThread(device)
        connectThread?.start()
    }

    @Synchronized
    fun stop() {
        connectThread?.cancel()
        connectThread = null
        connectedThread?.cancel()
        connectedThread = null
        _connectionState.value = STATE_NONE
    }

    fun write(out: ByteArray) {
        val r: ConnectedThread?
        synchronized(this) {
            if (_connectionState.value != STATE_CONNECTED) return
            r = connectedThread
        }
        r?.write(out)
    }

    @Synchronized
    private fun manageConnectedSocket(socket: BluetoothSocket) {
        Log.d("BluetoothService", "Connected")
        connectedThread = ConnectedThread(socket)
        connectedThread?.start()
        _connectionState.value = STATE_CONNECTED
    }

    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    throw SecurityException("Permission missing")
                }
                device.createRfcommSocketToServiceRecord(sppUuid)
            } catch (e: IOException) {
                Log.e("BluetoothService", "Socket create() failed", e)
                _errorMessage.tryEmit("Falló la creación del socket: ${e.message}")
                null
            }
        }

        override fun run() {
            _connectionState.value = STATE_CONNECTING
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                 // El escaneo debe detenerse antes de conectar
            }
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                try {
                    socket.connect()
                    manageConnectedSocket(socket)
                } catch (e: IOException) {
                    Log.e("BluetoothService", "Could not connect the client socket", e)
                     _errorMessage.tryEmit("No se pudo conectar: ${e.message}")
                    try {
                        socket.close()
                    } catch (e: IOException) {
                        Log.e("BluetoothService", "Could not close the client socket", e)
                    }
                    connectionFailed()
                    return
                }
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("BluetoothService", "Could not close the client socket", e)
            }
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            var numBytes: Int
            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer)
                    val data = mmBuffer.copyOf(numBytes)
                    if (!_receivedData.tryEmit(data)) {
                        Log.w("BluetoothService", "Failed to emit received data")
                    }
                } catch (e: IOException) {
                    Log.d("BluetoothService", "Input stream was disconnected", e)
                    connectionLost()
                    break
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e("BluetoothService", "Error occurred when sending data", e)
                _errorMessage.tryEmit("Error al enviar datos: ${e.message}")
                return
            }
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e("BluetoothService", "Could not close the connect socket", e)
            }
        }
    }

    private fun connectionFailed() {
        Log.d("BluetoothService", "Connection Failed")
        _connectionState.value = STATE_NONE
    }

    private fun connectionLost() {
        Log.d("BluetoothService", "Connection Lost")
        _errorMessage.tryEmit("Se perdió la conexión del dispositivo")
        _connectionState.value = STATE_NONE
    }
}
