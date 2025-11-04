package com.example.tott

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tott.ui.login.LoginScreen
import com.example.tott.ui.main.MainScreen
import com.example.tott.ui.register.RegisterScreen
import com.example.tott.ui.theme.TOTTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TOTTTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginClick = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick = {
                                navController.navigate("register")
                            },
                            onForgotPasswordClick = {
                                // TODO
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            // Le decimos qué hacer cuando el registro sea exitoso:
                            onRegisterSuccess = {
                                navController.popBackStack() // Vuelve a la pantalla anterior (Login)
                            }
                        )
                    }
                    composable("main") {
                        MainScreen()
                    }
                }
            }
        }
    }
}
