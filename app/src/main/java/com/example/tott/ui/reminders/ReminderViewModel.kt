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
            Reminder(userId = 1, dayOfWeek = "Lunes", hour = 21, minute = 0),
            Reminder(userId = 2, dayOfWeek = "Miércoles", hour = 10, minute = 30),
            Reminder(userId = 1, dayOfWeek = "Viernes", hour = 9, minute = 0, isActive = false) // Ejemplo de recordatorio pasado
        ))
    }

    fun addReminder(reminder: Reminder) {
        _reminders.add(reminder)
        // Aquí iría la lógica para programar la alarma con AlarmManager
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
