package com.tecsup.gymtracker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// La paleta organiza acciones, fondos y texto para mantener contraste en toda la app.
private val GymColors = lightColorScheme(
    primary = Color(0xFF2F6BEA),
    onPrimary = Color.White,
    secondary = Color(0xFF25B6A5),
    background = Color(0xFFF3F6FC),
    onBackground = Color(0xFF1B3C8C),
    surface = Color.White,
    onSurface = Color(0xFF1B3C8C),
    surfaceVariant = Color(0xFFEAF1FF),
    onSurfaceVariant = Color(0xFF6B7892)
)

// Material 3 entrega los colores comunes a botones, campos, menús y tarjetas.
@Composable
fun GymTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = GymColors, content = content)
}
