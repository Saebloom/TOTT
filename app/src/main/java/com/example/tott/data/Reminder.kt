package com.example.tott.data

import java.util.UUID

// Clase de datos para representar un recordatorio
data class Reminder(
    val id: String = UUID.randomUUID().toString(), // ID único para cada recordatorio
    val message: String,
    val hour: Int,
    val minute: Int,
    val isActive: Boolean = true // Para saber si es un recordatorio próximo o pasado
)
