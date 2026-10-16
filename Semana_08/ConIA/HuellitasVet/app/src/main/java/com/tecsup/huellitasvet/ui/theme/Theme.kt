package com.tecsup.huellitasvet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.huellitasvet.model.Rol

private val verde = Color(0xFF059669)
private val azul = Color(0xFF0284C7)
private val amarillo = Color(0xFFD89B00)
private val verdeClaro = Color(0xFFD1FAE5)
private val verdeFondo = Color(0xFFF6FDF9)
private val azulClaro = Color(0xFFE0F2FE)
private val azulFondo = Color(0xFFF0F9FF)
private val amarilloClaro = Color(0xFFFFF1BF)
private val amarilloFondo = Color(0xFFFFFBEB)

@Composable
fun VetTheme(rol: Rol = Rol.CLIENTE, oscuro: Boolean = false, contenido: @Composable () -> Unit) {
    val primario = when (rol) {
        Rol.CLIENTE -> verde
        Rol.VETERINARIO -> azul
        Rol.ADMIN -> amarillo
    }
    val contenedor = when (rol) {
        Rol.CLIENTE -> verdeClaro
        Rol.VETERINARIO -> azulClaro
        Rol.ADMIN -> amarilloClaro
    }
    val fondo = when (rol) {
        Rol.CLIENTE -> verdeFondo
        Rol.VETERINARIO -> azulFondo
        Rol.ADMIN -> amarilloFondo
    }
    val primarioOscuro = when (rol) {
        Rol.CLIENTE -> Color(0xFF6EE7B7)
        Rol.VETERINARIO -> Color(0xFF7DD3FC)
        Rol.ADMIN -> Color(0xFFFCD34D)
    }
    val contenedorOscuro = when (rol) {
        Rol.CLIENTE -> Color(0xFF064E3B)
        Rol.VETERINARIO -> Color(0xFF0C4A6E)
        Rol.ADMIN -> Color(0xFF713F12)
    }
    val colorTextoContenedor = when (rol) {
        Rol.CLIENTE -> Color(0xFF064E3B)
        Rol.VETERINARIO -> Color(0xFF075985)
        Rol.ADMIN -> Color(0xFF78350F)
    }
    val secundario = when (rol) {
        Rol.CLIENTE -> Color(0xFF2AA985)
        Rol.VETERINARIO -> Color(0xFF0369A1)
        Rol.ADMIN -> Color(0xFFB77900)
    }
    val variante = when (rol) {
        Rol.CLIENTE -> Color(0xFFEBF8F2)
        Rol.VETERINARIO -> Color(0xFFEAF6FD)
        Rol.ADMIN -> Color(0xFFFFF7D6)
    }
    val esquema = if (oscuro) darkColorScheme(
        primary = primarioOscuro,
        primaryContainer = contenedorOscuro,
        background = Color(0xFF101820), surface = Color(0xFF18232C),
        secondary = secundario
    ) else lightColorScheme(
        primary = primario,
        onPrimary = Color.White,
        primaryContainer = contenedor,
        onPrimaryContainer = colorTextoContenedor,
        secondary = secundario,
        background = fondo,
        surface = Color.White,
        surfaceVariant = variante,
        onSurface = Color(0xFF17212B),
        outline = Color(0xFFDCE4E8),
        error = Color(0xFFDC2626)
    )
    MaterialTheme(colorScheme = esquema, content = contenido)
}
