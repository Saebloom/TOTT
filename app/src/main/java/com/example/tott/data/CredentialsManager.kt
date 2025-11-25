package com.example.tott.data

class CredentialsManager {

    // Ya no necesitamos instanciar UserRepository() porque ahora es un object (Singleton)

    fun validateCredentials(email: String, password: String): User? {
        // Obtenemos la lista viva del UserRepository
        val users = UserRepository.getUsers()

        // Buscamos si existe coincidencia
        return users.find { it.email == email && it.password == password }
    }
}