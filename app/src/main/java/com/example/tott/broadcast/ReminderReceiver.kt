package com.example.tott.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.tott.R

// ===== DECLARACIÓN DE LA CLASE CORREGIDA =====
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Es hora de tu recordatorio"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear un canal de notificaciones (necesario para Android 8.0 Oreo y superior)
        val channel = NotificationChannel(
            "reminder_channel",
            "Recordatorios",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para los recordatorios de la app"
        }
        notificationManager.createNotificationChannel(channel)

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ¡Usa un ícono tuyo!
            .setContentTitle("Recordatorio")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // La notificación se cierra al tocarla
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }
}
