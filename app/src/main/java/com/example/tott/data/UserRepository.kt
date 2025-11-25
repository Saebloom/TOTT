package com.example.tott.data

import androidx.compose.ui.graphics.Color
import com.example.tott.R

// Cambiamos 'class' por 'object' para que sea un Singleton (una única instancia para toda la app)
object UserRepository {

    // Usamos mutableListOf para poder agregar usuarios nuevos dinámicamente
    private val users = mutableListOf(
        User(
            id = 1,
            email = "usuario.demo01@example.com",
            password = "Pruebas2024!",
            color = Color(0xFFB57F7F),
            name = "Nicolas Espejo",
            avatarRes = R.drawable.nicolas
        ),
        User(
            id = 2,
            email = "usuario.demo02@example.com",
            password = "DevTesting#45",
            color = Color(0xFF8F7FB5),
            name = "Eugenia Espinoza",
            avatarRes = R.drawable.eugenia
        )
    )

    fun getUsers(): List<User> {
        return users
    }

    // Nueva función para guardar usuarios registrados
    fun addUser(user: User) {
        users.add(user)
    }
}
