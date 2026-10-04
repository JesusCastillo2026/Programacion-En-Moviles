package com.saludplus.citas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Paleta visual de la versión ConIA: azul clínico, turquesa y superficies suaves. */
@Composable
fun SaludPlusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AzulSalud,
            onPrimary = Color.White,
            primaryContainer = AzulClaro,
            secondary = Turquesa,
            background = Fondo,
            surface = Color.White,
            onSurface = Texto
        ),
        content = content
    )
}
