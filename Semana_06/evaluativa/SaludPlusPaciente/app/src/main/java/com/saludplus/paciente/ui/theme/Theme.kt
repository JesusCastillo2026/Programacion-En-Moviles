package com.saludplus.paciente.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SaludPlusColors = lightColorScheme(
    primary = AzulClinico,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = VerdeExito,
    background = FondoClinico,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = TextoPrincipal,
    onBackground = TextoPrincipal,
    error = RojoAlerta
)

@Composable
fun SaludPlusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SaludPlusColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

