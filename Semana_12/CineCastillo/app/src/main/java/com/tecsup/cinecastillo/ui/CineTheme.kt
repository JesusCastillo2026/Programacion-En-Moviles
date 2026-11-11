package com.tecsup.cinecastillo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colors = lightColorScheme(
    primary = Color(0xFF5546A8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E3FF),
    background = Color(0xFFF8F7FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0EDF8),
    onSurface = Color(0xFF1E1B2A)
)

@Composable
fun CineTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, content = content)
}
