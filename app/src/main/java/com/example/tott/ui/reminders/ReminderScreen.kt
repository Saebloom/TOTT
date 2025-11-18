package com.example.tott.ui.reminders

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tott.data.Reminder
import com.example.tott.data.User
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    users: List<User>,
    onReminderAdded: (Reminder) -> Unit
) {
    var selectedUser by remember { mutableStateOf(users.firstOrNull()) }
    var selectedDay by remember { mutableStateOf("Lunes") }
    var hour by remember { mutableStateOf(12) }
    var minute by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            hour = selectedHour
            minute = selectedMinute
        },
        hour,
        minute,
        true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = {}
        ) {
            OutlinedTextField(
                value = selectedUser?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aquí irían los botones para seleccionar el día

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { timePickerDialog.show() }) {
            Text("Seleccionar hora")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (selectedUser != null) {
                    val reminder = Reminder(
                        userId = selectedUser!!.id,
                        dayOfWeek = selectedDay,
                        hour = hour,
                        minute = minute
                    )
                    onReminderAdded(reminder)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Recordatorio")
        }
    }
}
