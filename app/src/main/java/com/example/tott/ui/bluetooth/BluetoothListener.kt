package com.example.tott.ui.bluetooth

/**
 * Interface for receiving callbacks from the BluetoothService.
 * The ViewModel will implement this to be notified of connection events.
 */
interface BluetoothListener {
    /**
     * Called when a successful connection is established.
     */
    fun onConnected()

    /**
     * Called when the connection is lost or fails.
     */
    fun onDisconnected()

    /**
     * Called when a connection error occurs.
     * @param error A descriptive error message.
     */
    fun onConnectionError(error: String)

    /**
     * Called when a new message is received from the Bluetooth device.
     * @param message The received data as a String.
     */
    fun onMessageReceived(message: String)
}

