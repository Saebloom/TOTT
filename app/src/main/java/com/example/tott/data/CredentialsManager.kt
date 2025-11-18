package com.example.tott.data

/**
 * Gestor de credenciales que valida a los usuarios contra una lista estática.
 */
class CredentialsManager {

    private val userRepository = UserRepository()

    /**
     * Valida si el email y la contraseña proporcionados coinciden con los de un usuario estático.
     *
     * @param email El email a validar.
     * @param password La contraseña a validar.
     * @return El objeto User si las credenciales son válidas, `null` en caso contrario.
     */
    fun validateCredentials(email: String, password: String): User? {
        val users = userRepository.getStaticUsers()
        return users.find { it.email == email && it.password == password }
    }
}
