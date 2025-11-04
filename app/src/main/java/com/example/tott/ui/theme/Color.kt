package com.example.tott.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de Colores para TOTT (basada en los mockups)

// Verdes
val TottGreenPrimary = Color(0xFF388E3C) // Verde oscuro para botones e iconos
val TottGreenBackground = Color(0xFFE8F5E9) // Verde muy claro para el fondo de la pantalla

// Colores base
val TottWhite = Color(0xFFFFFFFF) // Para el fondo de las tarjetas
val TottBlack = Color(0xFF000000) // Para texto principal
val TottGrey = Color(0xFF49454F) // Para texto secundario o hints

// Estos son los nombres estándar para el tema de Material 3,
// que asignaremos en Theme.kt.
val md_theme_light_primary = TottGreenPrimary
val md_theme_light_onPrimary = TottWhite
val md_theme_light_background = TottGreenBackground
val md_theme_light_onBackground = TottBlack
val md_theme_light_surface = TottWhite // Fondo de las tarjetas
val md_theme_light_onSurface = TottBlack // Texto sobre las tarjetas
