package com.example.tott.ui.reminders

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.tott.data.Reminder

// ViewModel para gestionar la lógica y los datos de los recordatorios
class ReminderViewModel : ViewModel() {

    // Lista observable que almacenará nuestros recordatorios.
    // La UI se actualizará automáticamente cuando esta lista cambie.
    private val _reminders = mutableStateListOf<Reminder>()
    val reminders: List<Reminder> = _reminders

    init {
        // Añadir datos de ejemplo para empezar
        _reminders.addAll(listOf(
            Reminder(message = "Sacar la basura orgánica", hour = 21, minute = 0),
            Reminder(message = "Revisar nivel del compost", hour = 10, minute = 30),
            Reminder(message = "Llamar al servicio de reciclaje", hour = 9, minute = 0, isActive = false) // Ejemplo de recordatorio pasado
        ))
    }

    fun addReminder(message: String, hour: Int, minute: Int) {
        if (message.isNotBlank()) {
            val newReminder = Reminder(message = message, hour = hour, minute = minute)
            _reminders.add(newReminder)
            // Aquí iría la lógica para programar la alarma con AlarmManager
        }
    }

    fun deleteReminder(reminderId: String) {
        _reminders.removeAll { it.id == reminderId }
        // Aquí iría la lógica para cancelar la alarma con AlarmManager
    }

    // Función para simular que un recordatorio se ha completado
    fun markAsPast(reminderId: String) {
        val index = _reminders.indexOfFirst { it.id == reminderId }
        if (index != -1) {
            _reminders[index] = _reminders[index].copy(isActive = false)
        }
    }
}
