package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MascotaAvatar(especie: String, modifier: Modifier = Modifier, tamano: Dp = 56.dp) {
    Canvas(modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val piel = when (especie) {
            "GATO" -> Color(0xFFFFD18B)
            "PERRO" -> Color(0xFFD9A36A)
            else -> Color(0xFFD9C6FF)
        }
        drawCircle(Color(0xFFE7F7EE), radius = w * .49f, center = Offset(w / 2, h / 2))
        if (especie == "GATO") {
            val orejaI = Path().apply { moveTo(w * .20f, h * .42f); lineTo(w * .22f, h * .08f); lineTo(w * .45f, h * .32f); close() }
            val orejaD = Path().apply { moveTo(w * .80f, h * .42f); lineTo(w * .78f, h * .08f); lineTo(w * .55f, h * .32f); close() }
            drawPath(orejaI, piel, style = Fill)
            drawPath(orejaD, piel, style = Fill)
        } else {
            drawOval(piel.copy(alpha = .85f), topLeft = Offset(w * .10f, h * .30f), size = androidx.compose.ui.geometry.Size(w * .22f, h * .40f))
            drawOval(piel.copy(alpha = .85f), topLeft = Offset(w * .68f, h * .30f), size = androidx.compose.ui.geometry.Size(w * .22f, h * .40f))
        }
        drawCircle(piel, radius = w * .32f, center = Offset(w * .5f, h * .53f))
        drawCircle(Color(0xFF263238), radius = w * .035f, center = Offset(w * .39f, h * .49f))
        drawCircle(Color(0xFF263238), radius = w * .035f, center = Offset(w * .61f, h * .49f))
        drawCircle(Color(0xFF5D4037), radius = w * .045f, center = Offset(w * .5f, h * .63f))
    }
}
