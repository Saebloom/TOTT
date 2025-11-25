package com.example.tott.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.tott.ui.bluetooth.BluetoothListener
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothService(private val context: Context, private val listener: BluetoothListener) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null

    // UUID estándar para módulos seriales (HC-05, HC-06)
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // Variables para el Heartbeat
    private var lastHeartbeatTime: Long = 0
    private val HEARTBEAT_TIMEOUT = 8000L // 8 segundos
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoringHeartbeat = false

    private val heartbeatWatchdog = object : Runnable {
        override fun run() {
            if (isMonitoringHeartbeat) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastHeartbeatTime > HEARTBEAT_TIMEOUT) {
                    Log.e("BluetoothService", "TIMEOUT: No se recibió latido del Arduino")
                    disconnect()
                    runOnUi { listener.onDisconnected() }
                } else {
                    handler.postDelayed(this, 2000)
                }
            }
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): Set<BluetoothDevice>? {
        if (!hasPermissions()) return null
        return bluetoothAdapter?.bondedDevices
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        if (!hasPermissions()) return
        disconnect() // Cerrar conexiones previas
        connectThread = ConnectThread(device)
        connectThread?.start()
    }

    fun disconnect() {
        stopHeartbeatMonitor()
        connectThread?.cancel()
        connectedThread?.cancel()
        connectThread = null
        connectedThread = null
    }

    fun sendData(message: String) {
        connectedThread?.write(message.toByteArray())
    }

    private fun hasPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12+: Necesitamos CONNECT y SCAN
            val hasConnect = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            val hasScan = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            hasConnect && hasScan
        } else {
            // Android < 12: Necesitamos Ubicación para temas de Bluetooth
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startHeartbeatMonitor() {
        lastHeartbeatTime = System.currentTimeMillis()
        isMonitoringHeartbeat = true
        handler.post(heartbeatWatchdog)
    }

    private fun stopHeartbeatMonitor() {
        isMonitoringHeartbeat = false
        handler.removeCallbacks(heartbeatWatchdog)
    }

    private fun runOnUi(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post(action)
    }

    // --- HILO DE CONEXIÓN ---
    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        // Usamos 'lazy' con try-catch interno por seguridad al crear el socket
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            try {
                device.createRfcommSocketToServiceRecord(MY_UUID)
            } catch (e: SecurityException) {
                runOnUi { listener.onConnectionError("Error de permisos al crear socket") }
                null
            } catch (e: Exception) {
                runOnUi { listener.onConnectionError("Error general al crear socket") }
                null
            }
        }

        override fun run() {
            // Importante: Proteger cancelDiscovery
            try {
                bluetoothAdapter?.cancelDiscovery()
            } catch (e: SecurityException) {
                Log.e("BluetoothService", "No se tiene permiso para cancelar discovery (Scan)", e)
                // No retornamos, intentamos conectar igual
            }

            // Verificamos si el socket se creó bien antes de intentar conectar
            val socket = mmSocket
            if (socket == null) {
                return
            }

            try {
                // Esta es la llamada que bloquea, debe ir en el try
                socket.connect()
            } catch (e: Exception) {
                // Esto atrapa tanto IOException (error de conexión) como SecurityException
                Log.e("BluetoothService", "Error al conectar socket", e)
                runOnUi { listener.onConnectionError("No se pudo conectar. Verifica que el basurero esté encendido.") }
                try { socket.close() } catch (e2: Exception) { }
                return
            }

            // Si llegamos aquí, ¡éxito!
            connectedThread = ConnectedThread(socket)
            connectedThread?.start()
            runOnUi {
                listener.onConnected()
                startHeartbeatMonitor()
            }
        }

        fun cancel() { try { mmSocket?.close() } catch (e: Exception) { } }
    }

    // --- HILO DE COMUNICACIÓN ---
    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val buffer: ByteArray = ByteArray(1024)

        override fun run() {
            var numBytes: Int
            while (true) {
                try {
                    if (mmInStream.available() > 0) {
                        numBytes = mmInStream.read(buffer)
                        val readMessage = String(buffer, 0, numBytes)

                        if (readMessage.contains("STATUS:ACTIVE")) {
                            lastHeartbeatTime = System.currentTimeMillis()
                        } else {
                            runOnUi { listener.onMessageReceived(readMessage) }
                        }
                    } else {
                        sleep(100)
                    }
                } catch (e: IOException) {
                    runOnUi { listener.onDisconnected() }
                    break
                }
            }
        }

        fun write(bytes: ByteArray) {
            try { mmOutStream.write(bytes) } catch (e: IOException) { }
        }
        fun cancel() { try { mmSocket.close() } catch (e: IOException) { } }
    }
}