package com.tecsup.cinecastillo.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val colors = lightColorScheme(
    primary=Color(0xFF146C78), onPrimary=Color.White,
    primaryContainer=Color(0xFFD7F0EE), onPrimaryContainer=Color(0xFF103D46),
    secondary=Color(0xFF96522D), secondaryContainer=Color(0xFFFFE7CE), onSecondaryContainer=Color(0xFF49250F),
    background=Color(0xFFF4F8FA), onBackground=Color(0xFF142C40),
    surface=Color.White, surfaceVariant=Color(0xFFE8F1F5),
    surfaceContainer=Color(0xFFEDF5F6), surfaceContainerLow=Color(0xFFF5FAFB),
    surfaceContainerHigh=Color(0xFFE2EFF0), onSurface=Color(0xFF142C40),
    onSurfaceVariant=Color(0xFF4B6574), outline=Color(0xFF79949E)
)
@Composable fun CineTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme=colors,shapes=Shapes(small=RoundedCornerShape(12.dp),medium=RoundedCornerShape(20.dp),large=RoundedCornerShape(28.dp)),content=content)
}
