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
    // Lógica para que el calendario sea interactivo
    var currentMonth by remember { mutableStateOf(YearMonth.of(2025, 8)) } // Fijado a Agosto 2025 como en el mockup

    val calendarDays = remember(currentMonth) {
        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Dom=0, Lun=1...
        val daysInMonth = currentMonth.lengthOfMonth()

        val days = mutableListOf<CalendarDay>()
        // Rellenar espacios vacíos al principio del mes
        for (i in 0 until firstDayOfWeek) {
            days.add(CalendarDay(""))
        }
        // Rellenar los días del mes
        for (day in 1..daysInMonth) {
            // Lógica para asignar usuarios solo en Agosto 2025, como en el mockup
            val user = if (currentMonth.year == 2025 && currentMonth.monthValue == 8) {
                when (day) {
                    2, 12, 22 -> userNicolas
                    7, 17 -> userEugenia
                    else -> null
                }
            } else {
                null
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
            Text("Select date", style = MaterialTheme.typography.bodySmall)
            Text("Mon, Aug 17", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.forLanguageTag("es"))),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                // Botones de texto para evitar errores de iconos
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text(">", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                val daysOfWeek = listOf("D", "L", "M", "M", "J", "V", "S") // Días en español
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

@Preview(showBackground = true, locale = "es")
@Composable
fun UsersScreenPreview() {
    TOTTTheme {
        UsersScreen()
    }
}
