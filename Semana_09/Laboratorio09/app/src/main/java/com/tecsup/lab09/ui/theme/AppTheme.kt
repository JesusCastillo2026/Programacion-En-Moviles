package com.tecsup.lab09.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LabColors = lightColorScheme(
    primary = Color(0xFF3156B8),
    onPrimary = Color.White,
    secondary = Color(0xFF168A7A),
    background = Color(0xFFF4F6FB),
    surface = Color.White,
    onSurface = Color(0xFF1B2432),
    error = Color(0xFFB3261E)
)

@Composable
fun Lab09Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LabColors,
        content = content
    )
}
