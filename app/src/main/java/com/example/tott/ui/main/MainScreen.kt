package com.example.tott.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.tott.ui.theme.TOTTTheme

// Data class para representar cada ítem de la barra de navegación
private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // El índice 0 corresponde a "Usuarios" para que sea la pantalla inicial al abrir
    var selectedItemIndex by remember { mutableStateOf(0) }

    val items = listOf(
        // *** CAMBIO: Apuntando a la nueva UsersScreen del archivo UsersScreen.kt ***
        BottomNavItem("Usuarios", Icons.Default.Person, { UsersScreen() }),
        BottomNavItem("Estado del basurero", Icons.Default.Home, { TrashStatusView() }),
        BottomNavItem("Recordatorios", Icons.Default.DateRange, { RemindersScreen() })
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        label = { Text(item.label) },
                        alwaysShowLabel = true,
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            items[selectedItemIndex].screen()
        }
    }
}

// --- Pantallas de Marcador de Posición ---

// *** ELIMINADO: La función UsersScreen() de placeholder ya no es necesaria ***

@Composable
private fun RemindersScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de Recordatorios (próximamente)")
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TOTTTheme {
        MainScreen()
    }
}
