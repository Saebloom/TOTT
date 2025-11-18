package com.example.tott.ui.main

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tott.data.Reminder
import com.example.tott.data.User
import com.example.tott.ui.reminders.ReminderViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    users: List<User>,
    onReminderAdded: (Reminder) -> Unit,
    viewModel: ReminderViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Próximos", "Pasados")

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
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> ReminderList(
                    reminders = upcomingReminders,
                    users = users,
                    onDelete = { viewModel.deleteReminder(it) }
                )
                1 -> ReminderList(
                    reminders = pastReminders,
                    users = users,
                    onDelete = { viewModel.deleteReminder(it) }
                )
            }
        }
    }

    if (showDialog) {
        AddReminderDialog(
            users = users,
            onDismiss = { showDialog = false },
            onConfirm = { reminder ->
                onReminderAdded(reminder)
                showDialog = false
            }
        )
    }
}

@Composable
fun ReminderList(reminders: List<Reminder>, users: List<User>, onDelete: (String) -> Unit) {
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
                val user = users.find { it.id == reminder.userId }
                ReminderItem(reminder = reminder, user = user, onDelete = { onDelete(reminder.id) })
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder, user: User?, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            user?.let {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(it.color)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recordatorio para ${user?.name ?: "desconocido"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Día: ${reminder.dayOfWeek} a las %02d:%02d".format(reminder.hour, reminder.minute),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar recordatorio", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    users: List<User>,
    onDismiss: () -> Unit,
    onConfirm: (Reminder) -> Unit
) {
    val context = LocalContext.current
    var selectedUser by remember { mutableStateOf(users.firstOrNull()) }
    var isUserDropdownExpanded by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    var selectedDay by remember { mutableStateOf(daysOfWeek.first()) }
    var isDayDropdownExpanded by remember { mutableStateOf(false) }

    var hour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
    var timeSet by remember { mutableStateOf(false) }

    val timePickerDialog = TimePickerDialog(
        context, { _, selectedHour, selectedMinute ->
            hour = selectedHour
            minute = selectedMinute
            timeSet = true
        }, hour, minute, true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Recordatorio") },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                // User Selector
                ExposedDropdownMenuBox(
                    expanded = isUserDropdownExpanded,
                    onExpandedChange = { isUserDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedUser?.name ?: "Seleccionar usuario",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Usuario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUserDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isUserDropdownExpanded,
                        onDismissRequest = { isUserDropdownExpanded = false }
                    ) {
                        users.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.name) },
                                onClick = {
                                    selectedUser = user
                                    isUserDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Day Selector
                ExposedDropdownMenuBox(
                    expanded = isDayDropdownExpanded,
                    onExpandedChange = { isDayDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDay,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Día de la semana") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDayDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isDayDropdownExpanded,
                        onDismissRequest = { isDayDropdownExpanded = false }
                    ) {
                        daysOfWeek.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    selectedDay = day
                                    isDayDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Time Picker
                Button(onClick = { timePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (timeSet) "Hora: %02d:%02d".format(hour, minute) else "Seleccionar Hora")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedUser?.let { user ->
                        val newReminder = Reminder(
                            userId = user.id,
                            dayOfWeek = selectedDay,
                            hour = hour,
                            minute = minute
                        )
                        onConfirm(newReminder)
                    }
                },
                enabled = selectedUser != null && timeSet
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
