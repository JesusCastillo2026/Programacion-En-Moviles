package com.tecsup.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colores = lightColorScheme(
    primary = Color(0xFF1769AA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5E8FA),
    onPrimaryContainer = Color(0xFF102E46),
    secondary = Color(0xFF137C83),
    onSecondary = Color.White,
    background = Color(0xFFF3F6FA),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8EEF4),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFCE8E6)
)

@Composable
fun AvisosCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colores, content = content)
}
