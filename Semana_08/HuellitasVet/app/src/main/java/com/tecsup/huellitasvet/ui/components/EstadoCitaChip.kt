package com.tecsup.huellitasvet.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun EstadoCitaChip(estado: String) {
    val color = when (estado) {
        "PENDIENTE" -> Color(0xFFD97706)
        "CONFIRMADA" -> Color(0xFF16A34A)
        "ATENDIDA" -> Color(0xFF475569)
        else -> Color(0xFFDC2626)
    }
    AssistChip(onClick = {}, label = { Text(estado.lowercase()) }, colors = AssistChipDefaults.assistChipColors(labelColor = color))
}
