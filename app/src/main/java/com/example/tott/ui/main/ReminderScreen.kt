package com.example.tott.ui.main

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tott.data.Reminder
import com.example.tott.ui.reminders.ReminderViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    // Inyectamos el ViewModel
    viewModel: ReminderViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Próximos", "Pasados")

    // Filtramos las listas de recordatorios
    val upcomingReminders = viewModel.reminders.filter { it.isActive }
    val pastReminders = viewModel.reminders.filter { !it.isActive }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Recordatorio")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Pestañas para "Próximos" y "Pasados"
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Contenido de la pestaña seleccionada
            when (selectedTabIndex) {
                0 -> ReminderList(
                    reminders = upcomingReminders,
                    onDelete = { viewModel.deleteReminder(it) }
                )
                1 -> ReminderList(
                    reminders = pastReminders,
                    onDelete = { viewModel.deleteReminder(it) }
                )
            }
        }
    }

    // Diálogo para crear un nuevo recordatorio
    if (showDialog) {
        AddReminderDialog(
            onDismiss = { showDialog = false },
            onConfirm = { message, hour, minute ->
                viewModel.addReminder(message, hour, minute)
                showDialog = false
            }
        )
    }
}

// Composable para mostrar la lista de recordatorios
@Composable
fun ReminderList(reminders: List<Reminder>, onDelete: (String) -> Unit) {
    if (reminders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay recordatorios aquí.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reminders, key = { it.id }) { reminder ->
                ReminderItem(reminder = reminder, onDelete = { onDelete(reminder.id) })
            }
        }
    }
}

// Composable para un único ítem de la lista
@Composable
fun ReminderItem(reminder: Reminder, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(reminder.message, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Programado para las %02d:%02d".format(reminder.hour, reminder.minute),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar recordatorio", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// Composable para el diálogo de añadir recordatorio
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (message: String, hour: Int, minute: Int) -> Unit
) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }
    var timeSet by remember { mutableStateOf(false) }

    val timePickerDialog = TimePickerDialog(
        context, { _, selectedHour, selectedMinute ->
            hour = selectedHour
            minute = selectedMinute
            timeSet = true
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Recordatorio") },
        text = {
            Column {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mensaje") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { timePickerDialog.show() }) {
                    Text(if (timeSet) "Hora: %02d:%02d".format(hour, minute) else "Seleccionar Hora")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(message, hour, minute) },
                enabled = message.isNotBlank() && timeSet
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
