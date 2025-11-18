package com.example.tott.ui.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tott.data.Reminder
import com.example.tott.data.User
import java.util.Calendar

@Composable
fun CalendarScreen(reminders: List<Reminder>, users: List<User>) {
    val daysInMonth = (1..31).toList() // Simplificado para el ejemplo

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(16.dp)
    ) {
        items(daysInMonth) { day ->
            val reminderForDay = reminders.find { it.dayOfWeek == day.toString() } // Simplificado
            val user = users.find { it.id == reminderForDay?.userId }

            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(user?.color ?: Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = day.toString())
                }
            }
        }
    }
}
