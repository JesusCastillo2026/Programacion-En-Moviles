package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EstadoCitaChip(estado: String) {
    val (color, fondo, etiqueta) = when (estado) {
        "PENDIENTE" -> Triple(Color(0xFFD97706), Color(0xFFFFF2C6), "Pendiente")
        "CONFIRMADA" -> Triple(Color(0xFF16A34A), Color(0xFFD8F5E8), "Confirmada")
        "ATENDIDA" -> Triple(Color(0xFF475569), Color(0xFFE9EEF2), "Atendida")
        else -> Triple(Color(0xFFDC2626), Color(0xFFFEE4E2), "Cancelada")
    }
    Text(etiqueta, Modifier.background(fondo, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 5.dp), color = color, fontWeight = FontWeight.SemiBold, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
}
