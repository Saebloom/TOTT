package com.example.tott.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tott.R
import com.example.tott.broadcast.ReminderScheduler
import com.example.tott.data.User
import com.example.tott.data.UserRepository
import com.example.tott.ui.bluetooth.BluetoothViewModel
import com.example.tott.ui.forgotpassword.ForgotPasswordScreen
import com.example.tott.ui.login.LoginScreen
import com.example.tott.ui.main.MainScreen
import com.example.tott.ui.main.ReminderScreen
import com.example.tott.ui.register.RegisterScreen
import com.example.tott.ui.reminders.ReminderViewModel
import com.example.tott.ui.theme.TOTTTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reminderScheduler = ReminderScheduler(this)
        enableEdgeToEdge() // Habilita pantalla completa (barra transparente)

        setContent {
            TOTTTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // --- 1. SOLICITUD DE PERMISOS ---
                // Esto es fundamental para que el Bluetooth funcione en celulares modernos
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions.entries.all { it.value }
                    if (!granted) {
                        Toast.makeText(context, "Se necesitan permisos de Bluetooth", Toast.LENGTH_LONG).show()
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Android 12 o superior necesita permisos explícitos de escaneo y conexión
                        permissionLauncher.launch(arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ))
                    } else {
                        // Android antiguo usa permisos de ubicación para escanear BT
                        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                    }
                }

                // --- 2. INICIALIZAR VIEWMODELS ---
                val reminderViewModel: ReminderViewModel = viewModel()

                // Inicializamos el BluetoothViewModel con su Factory (necesario para pasar el contexto)
                val bluetoothViewModel: BluetoothViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return BluetoothViewModel(context) as T
                        }
                    }
                )

                // Obtenemos la lista de usuarios (¡Ahora es dinámica gracias a tu cambio en UserRepository!)
                val users = UserRepository.getUsers()
                val reminders = reminderViewModel.reminders

                // --- 3. NAVEGACIÓN ---
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // Pantalla de Login
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick = { navController.navigate("register") },
                            onForgotPasswordClick = { navController.navigate("forgot_password") }
                        )
                    }

                    // Pantalla Principal (Donde está el control del basurero)
                    composable("main") {
                        MainScreen(
                            navController = navController,
                            users = users,
                            reminders = reminders,
                            bluetoothViewModel = bluetoothViewModel // ¡Aquí pasamos el control BT a la pantalla!
                        )
                    }

                    // Pantalla de Configuración de Recordatorios
                    composable("reminder_config") {
                        ReminderScreen(
                            users = users,
                            onReminderAdded = {
                                reminderViewModel.addReminder(it)
                                reminderScheduler.schedule(it)
                                Toast.makeText(context, "Recordatorio guardado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        )
                    }

                    // Pantalla de Registro (Aquí está la magia para guardar usuarios)
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { name, email, pass ->
                                // 1. Creamos el usuario nuevo con los datos del formulario
                                val newUser = User(
                                    id = Random.nextInt(100, 10000), // Generamos un ID aleatorio
                                    email = email,
                                    password = pass,
                                    name = name,
                                    color = Color.Gray, // Color por defecto
                                    avatarRes = R.drawable.ic_launcher_foreground // Avatar por defecto
                                )

                                // 2. ¡Lo guardamos en la memoria de la app!
                                UserRepository.addUser(newUser)

                                // 3. Avisamos y volvemos atrás para que se loguee
                                Toast.makeText(context, "¡Usuario creado! Inicia sesión.", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            },
                            onLoginClick = { navController.popBackStack() }
                        )
                    }

                    // Pantalla de Olvido de Contraseña
                    composable("forgot_password") {
                        ForgotPasswordScreen(
                            onBackClick = { navController.popBackStack() },
                            onSendClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}