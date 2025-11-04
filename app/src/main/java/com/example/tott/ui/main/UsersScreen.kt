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
import com.example.tott.R
import com.example.tott.ui.theme.TOTTTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

// --- Datos de Ejemplo ---
data class User(val name: String, val color: Color, val avatarRes: Int)
data class CalendarDay(val day: String, val user: User? = null)

val userNicolas = User("Nicolas Espejo", Color(0xFFB57F7F), R.drawable.ic_launcher_foreground) // Color café
val userEugenia = User("Eugenia Espinoza", Color(0xFF8F7FB5), R.drawable.ic_launcher_background) // Color morado

val users = listOf(userNicolas, userEugenia)

// --- Pantalla Principal de Usuarios ---
@Composable
fun UsersScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalendarView()
        UserListView()
        Spacer(modifier = Modifier.weight(1f))
        val context = LocalContext.current
        Button(onClick = {
            Toast.makeText(context, "Enlace de invitación copiado (simulación)", Toast.LENGTH_SHORT).show()
        }) {
            Text("Invitar a otro usuario")
        }
    }
}

// --- Componentes de la pantalla ---

@Composable
private fun CalendarView() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    val calendarDays = remember(currentMonth) {
        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = currentMonth.lengthOfMonth()
        val days = mutableListOf<CalendarDay>()
        for (i in 0 until firstDayOfWeek) {
            days.add(CalendarDay(""))
        }
        for (day in 1..daysInMonth) {
            val user = when ((day + currentMonth.monthValue) % 10) { // Lógica de ejemplo para variar los días
                2 -> userNicolas
                7 -> userEugenia
                else -> null
            }
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
            val headerDateFormatter = DateTimeFormatter.ofPattern("E, MMM d").withLocale(Locale.getDefault())
            Text(today.format(headerDateFormatter), style = MaterialTheme.typography.bodySmall)
            Text(today.format(DateTimeFormatter.ofPattern("EEEE")), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.getDefault())),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                // Botones de texto para evitar errores de iconos
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("<")
                }
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text(">")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                daysOfWeek.forEach {
                    Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                modifier = Modifier.size(32.dp).background(day.user.color, CircleShape),
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
private fun UserListView() {
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
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(user.name, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsersScreenPreview() {
    TOTTTheme {
        UsersScreen()
    }
}
