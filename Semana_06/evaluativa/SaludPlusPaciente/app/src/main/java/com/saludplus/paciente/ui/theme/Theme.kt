package com.saludplus.paciente.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val SaludPlusColors = lightColorScheme(
    primary = AzulClinico,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = VerdeExito,
    primaryContainer = CelesteSuave,
    onPrimaryContainer = AzulProfundo,
    secondaryContainer = Color(0xFFDDF5ED),
    onSecondaryContainer = Color(0xFF07533E),
    background = FondoClinico,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario,
    outlineVariant = Color(0xFFD7E1EE),
    surfaceVariant = CelesteSuave,
    errorContainer = Color(0xFFFFEDEA),
    onErrorContainer = Color(0xFF8C2727),
    onBackground = TextoPrincipal,
    error = RojoAlerta
)

@Composable
fun SaludPlusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SaludPlusColors,
        typography = SaludPlusTypography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(30.dp)
        ),
        content = content
    )
}

