package com.example.tott.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tott.data.Reminder
import com.example.tott.data.User
import com.example.tott.data.UserRepository
import com.example.tott.ui.bluetooth.BluetoothViewModel
import com.example.tott.ui.components.TrashControlCard
import com.example.tott.ui.theme.TOTTTheme

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    users: List<User>,
    reminders: List<Reminder>,
    bluetoothViewModel: BluetoothViewModel // <--- 1. Recibimos el ViewModel aquí
) {
    var selectedItemIndex by remember { mutableStateOf(0) }

    val items = listOf(
        BottomNavItem("Usuarios", Icons.Default.Person, "users_tab"),
        BottomNavItem("Estado del basurero", Icons.Default.Home, "status_tab"),
        BottomNavItem("Recordatorios", Icons.Default.DateRange, "reminder_config")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            if (item.route == "reminder_config") {
                                navController.navigate(item.route)
                            } else {
                                selectedItemIndex = index
                            }
                        },
                        label = { Text(item.label) },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItemIndex) {
                0 -> UsersScreen(users = users, reminders = reminders)

                // <--- 2. Aquí conectamos la tarjeta de control real
                1 -> TrashControlCard(
                    viewModel = bluetoothViewModel,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
