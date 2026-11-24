package com.tecsup.murocampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colores = lightColorScheme(
    primary = Color(0xFF1976B2),
    onPrimary = Color.White,
    secondary = Color(0xFF3B82B8),
    background = Color(0xFFF8F9FC),
    surface = Color.White,
    onSurface = Color(0xFF202A36),
    error = Color(0xFFB3261E)
)

@Composable
fun MuroCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colores, content = content)
}
