package com.example.tott

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tott.ui.forgotpassword.ForgotPasswordScreen // Asumiendo que tienes esta pantalla
import com.example.tott.ui.login.LoginScreen
import com.example.tott.ui.main.MainScreen
import com.example.tott.ui.main.ReminderScreen // La pantalla de configurar recordatorio
import com.example.tott.ui.register.RegisterScreen
import com.example.tott.ui.theme.TOTTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TOTTTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                NavHost(
                    navController = navController,
                    startDestination = "login" // La app siempre inicia en el login
                ) {
                    // 1. Ruta de Login
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                // Al loguearse, vamos al MENÚ PRINCIPAL (el que tiene el calendario)
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick = { navController.navigate("register") },
                            onForgotPasswordClick = { navController.navigate("forgot_password") }
                        )
                    }

                    // 2. Ruta del Menú Principal (Dashboard)
                    composable("main") {
                        // Le pasamos el navController para que pueda navegar desde la barra inferior
                        MainScreen(navController = navController)
                    }

                    // 3. Ruta para la pantalla de "Configurar Recordatorio"
                    composable("reminder_config") {
                        ReminderScreen()
                    }

                    // 4. Ruta para el Registro de Usuario
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { navController.popBackStack() },
                            onLoginClick = { navController.popBackStack() }
                        )
                    }

                    // 5. Ruta para Recuperar Contraseña
                    composable("forgot_password") {
                        // Necesitarás crear este Composable si no lo tienes
                        ForgotPasswordScreen(
                            onBackClick = { navController.popBackStack() },
                            onSendClick = { email ->
                                Toast.makeText(context, "Enlace enviado a $email", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
