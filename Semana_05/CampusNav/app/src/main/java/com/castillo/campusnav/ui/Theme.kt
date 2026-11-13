package com.castillo.campusnav.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF22264D)
val Violet = Color(0xFF6457DA)
val Teal = Color(0xFF007F75)

/** Paleta fija para conservar el diseño en dispositivos con colores dinámicos distintos. */
@Composable
fun CampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Violet, onPrimary = Color.White,
            primaryContainer = Color(0xFFEEEBFF), onPrimaryContainer = Ink,
            secondary = Teal, secondaryContainer = Color(0xFFD7F4EB),
            background = Color(0xFFF5F6FB), surface = Color(0xFFF5F6FB),
            surfaceContainer = Color.White, surfaceContainerLow = Color.White,
            onSurface = Ink, onSurfaceVariant = Color(0xFF646981),
            outline = Color(0xFF9296AB), outlineVariant = Color(0xFFE2E4EF)
        ),
        typography = Typography(
            headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp),
            headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
            titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
            titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 23.sp)
        ),
        content = content
    )
}

