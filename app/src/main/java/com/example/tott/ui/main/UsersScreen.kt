package com.example.tott.ui.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tott.data.Reminder
import com.example.tott.data.User
import com.example.tott.data.UserRepository
import com.example.tott.ui.theme.TOTTTheme
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class CalendarDay(val day: String, val user: User? = null)

@Composable
fun UsersScreen(users: List<User>, reminders: List<Reminder>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalendarView(users = users, reminders = reminders)
        UserListView(users = users)
        Spacer(modifier = Modifier.weight(1f))
        val context = LocalContext.current
        Button(onClick = {
            Toast.makeText(context, "Enlace de invitación copiado (simulación)", Toast.LENGTH_SHORT).show()
        }) {
            Text("Invitar a otro usuario")
        }
    }
}

@Composable
private fun CalendarView(users: List<User>, reminders: List<Reminder>) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val calendarDays = remember(currentMonth, users, reminders) {
        val firstDayOfMonth = currentMonth.atDay(1)
        // Adjust for Sunday-start week if needed, but Locale default is usually fine
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // MONDAY is 1, SUNDAY is 7
        val daysInMonth = currentMonth.lengthOfMonth()

        val days = mutableListOf<CalendarDay>()
        // Add blank cells for days before the first of the month
        for (i in 1 until firstDayOfWeek) {
            days.add(CalendarDay(""))
        }

        for (day in 1..daysInMonth) {
            val date = currentMonth.atDay(day)
            val dayOfWeekSpanish = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))

            // Find the first active reminder for that day of the week
            val reminderForDay = reminders.find { it.dayOfWeek.equals(dayOfWeekSpanish, ignoreCase = true) && it.isActive }
            val user = reminderForDay?.let { rem -> users.find { it.id == rem.userId } }

            days.add(CalendarDay(day.toString(), user))
        }
        days
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale("es"))),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text(">", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                val daysOfWeek = listOf("L", "M", "X", "J", "V", "S", "D")
                daysOfWeek.forEach {
                    Text(it, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                userScrollEnabled = false
            ) {
                items(calendarDays) { day ->
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                        if (day.user != null) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(day.user.color, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(day.day, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(day.day)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserListView(users: List<User>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        users.forEach { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = user.color)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = user.avatarRes),
                        contentDescription = "Avatar de ${user.name}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(user.name, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "es")
@Composable
fun UsersScreenPreview() {
    TOTTTheme {
        UsersScreen(users = UserRepository.getUsers(), reminders = emptyList())
    }
}
