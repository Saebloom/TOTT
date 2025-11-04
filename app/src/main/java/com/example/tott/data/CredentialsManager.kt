package com.example.tott.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Gestor de credenciales que utiliza EncryptedSharedPreferences para almacenar
 * y recuperar de forma segura el email y la contraseña del usuario.
 *
 * @param context El contexto de la aplicación, necesario para crear el archivo de preferencias.
 */
class CredentialsManager(context: Context) {

    // Nombre del archivo de preferencias encriptadas y claves para los datos.
    private companion object {
        const val FILE_NAME = "tott_secure_prefs"
        const val KEY_EMAIL = "user_email"
        const val KEY_PASSWORD = "user_password"
    }

    // 1. Crear o recuperar la clave de cifrado maestra.
    // Esta clave se almacena de forma segura en el Keystore de Android.
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // 2. Inicializar EncryptedSharedPreferences.
    // Este objeto se comporta como un SharedPreferences normal, pero cifra
    // automáticamente las claves y los valores antes de escribirlos en el disco.
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        FILE_NAME,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        // *** LÍNEA CORREGIDA ***
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda el email y la contraseña del usuario de forma encriptada.
     * Esto se llamaría durante el registro.
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     */
    fun saveCredentials(email: String, password: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply() // Usa apply() para una escritura asíncrona en segundo plano.
        }
    }

    /**
     * Valida si el email y la contraseña proporcionados coinciden con los almacenados.
     * Esto se llamaría durante el inicio de sesión.
     * @param email El email a validar.
     * @param password La contraseña a validar.
     * @return `true` si las credenciales son válidas, `false` en caso contrario.
     */
    fun validateCredentials(email: String, password: String): Boolean {
        val storedEmail = sharedPreferences.getString(KEY_EMAIL, null)
        val storedPassword = sharedPreferences.getString(KEY_PASSWORD, null)

        // Compara las credenciales proporcionadas con las almacenadas.
        return email == storedEmail && password == storedPassword
    }

    /**
     * Borra todas las credenciales almacenadas de forma segura.
     * Esto podría llamarse cuando el usuario cierra sesión.
     */
    fun clearCredentials() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
