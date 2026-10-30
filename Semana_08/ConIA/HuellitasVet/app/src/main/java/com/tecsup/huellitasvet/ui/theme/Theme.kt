package com.tecsup.huellitasvet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.huellitasvet.model.Rol

private val verde = Color(0xFF059669)
private val azul = Color(0xFF1D4ED8)
private val verdeClaro = Color(0xFFD1FAE5)
private val verdeFondo = Color(0xFFF6FDF9)
private val azulClaro = Color(0xFFDBEAFE)
private val azulFondo = Color(0xFFF7F9FF)

@Composable
fun VetTheme(rol: Rol = Rol.CLIENTE, oscuro: Boolean = false, contenido: @Composable () -> Unit) {
    val primario = if (rol == Rol.ADMIN) azul else verde
    val contenedor = if (rol == Rol.ADMIN) azulClaro else verdeClaro
    val fondo = if (rol == Rol.ADMIN) azulFondo else verdeFondo
    val esquema = if (oscuro) darkColorScheme(
        primary = if (rol == Rol.ADMIN) Color(0xFF93B4FF) else Color(0xFF6EE7B7),
        primaryContainer = if (rol == Rol.ADMIN) Color(0xFF193B78) else Color(0xFF064E3B),
        background = Color(0xFF101820), surface = Color(0xFF18232C),
        secondary = if (rol == Rol.ADMIN) azul else verde
    ) else lightColorScheme(
        primary = primario,
        onPrimary = Color.White,
        primaryContainer = contenedor,
        onPrimaryContainer = if (rol == Rol.ADMIN) Color(0xFF12326F) else Color(0xFF064E3B),
        secondary = if (rol == Rol.ADMIN) Color(0xFF4775E8) else Color(0xFF2AA985),
        background = fondo,
        surface = Color.White,
        surfaceVariant = if (rol == Rol.ADMIN) Color(0xFFEEF3FD) else Color(0xFFEBF8F2),
        onSurface = Color(0xFF17212B),
        outline = Color(0xFFDCE4E8),
        error = Color(0xFFDC2626)
    )
    MaterialTheme(colorScheme = esquema, content = contenido)
}
