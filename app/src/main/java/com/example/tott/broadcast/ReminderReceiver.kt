package com.example.tott.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tott.data.NotificationService

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Es hora de tu recordatorio"
        val title = "Recordatorio de Basura"

        // Usar el NotificationService para mostrar la notificación
        val notificationService = NotificationService(context)
        notificationService.showNotification(title, message)
    }
}
