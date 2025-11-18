package com.example.tott

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tott.broadcast.ReminderScheduler
import com.example.tott.data.UserRepository
import com.example.tott.ui.forgotpassword.ForgotPasswordScreen
import com.example.tott.ui.login.LoginScreen
import com.example.tott.ui.main.MainScreen
import com.example.tott.ui.main.ReminderScreen
import com.example.tott.ui.register.RegisterScreen
import com.example.tott.ui.reminders.ReminderViewModel
import com.example.tott.ui.theme.TOTTTheme

class MainActivity : ComponentActivity() {
    private val reminderViewModel: ReminderViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reminderScheduler = ReminderScheduler(this)
        enableEdgeToEdge()
        setContent {
            TOTTTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val userRepository = UserRepository()
                val users = userRepository.getStaticUsers()
                val reminders = reminderViewModel.reminders

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
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

                    composable("main") {
                        MainScreen(navController = navController, users = users, reminders = reminders)
                    }

                    composable("reminder_config") {
                        ReminderScreen(
                            users = users,
                            onReminderAdded = {
                                reminderViewModel.addReminder(it)
                                reminderScheduler.schedule(it)
                                Toast.makeText(context, "Recordatorio guardado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            viewModel = reminderViewModel
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { navController.popBackStack() },
                            onLoginClick = { navController.popBackStack() }
                        )
                    }

                    composable("forgot_password") {
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
