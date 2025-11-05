package com.example.tott.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tott.ui.theme.TOTTTheme

// Data class para los ítems de la barra de navegación
private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) { // Recibe el NavController principal
    // El índice de la pestaña activa (0="Usuarios", 1="Estado")
    var selectedItemIndex by remember { mutableStateOf(0) }

    val items = listOf(
        BottomNavItem("Usuarios", Icons.Default.Person, "users_tab"),
        BottomNavItem("Estado del basurero", Icons.Default.Home, "status_tab"),
        BottomNavItem("Recordatorios", Icons.Default.DateRange, "reminder_config") // Ruta para NAVEGAR
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            if (item.route == "reminder_config") {
                                // ¡ACCIÓN CLAVE! Usamos el NavController para ir a otra pantalla
                                navController.navigate(item.route)
                            } else {
                                // Para "Usuarios" y "Estado", solo cambiamos la pestaña interna
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
        // Contenedor que muestra la pantalla de la pestaña activa
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItemIndex) {
                0 -> UsersScreen()         // Muestra la pantalla del calendario
                1 -> TrashStatusView()    // Muestra la pantalla del estado del basurero
            }
        }
    }
}

// VISTA PREVIA
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TOTTTheme {
        MainScreen(navController = rememberNavController())
    }
}
