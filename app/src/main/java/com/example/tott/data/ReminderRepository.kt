package com.example.tott.data

class ReminderRepository {
    private val reminders = mutableListOf<Reminder>()

    fun addReminder(reminder: Reminder) {
        reminders.add(reminder)
    }

    fun getReminders(): List<Reminder> {
        return reminders
    }
}
