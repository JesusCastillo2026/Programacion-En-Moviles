package com.tecsup.huellitasvet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.huellitasvet.model.Rol

private val verde = Color(0xFF059669)
private val azul = Color(0xFF1D4ED8)

@Composable
fun VetTheme(rol: Rol = Rol.CLIENTE, oscuro: Boolean = false, contenido: @Composable () -> Unit) {
    val primario = if (rol == Rol.ADMIN) azul else verde
    val contenedor = if (rol == Rol.ADMIN) Color(0xFFDBEAFE) else Color(0xFFD1FAE5)
    val fondo = if (rol == Rol.ADMIN) Color(0xFFF7F9FF) else Color(0xFFF6FDF9)
    val esquema = if (oscuro) darkColorScheme(primary = primario, primaryContainer = contenedor)
    else lightColorScheme(primary = primario, primaryContainer = contenedor, background = fondo)
    MaterialTheme(colorScheme = esquema, content = contenido)
}
