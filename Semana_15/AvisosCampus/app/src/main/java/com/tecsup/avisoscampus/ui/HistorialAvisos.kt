package com.tecsup.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    if (avisos.isEmpty()) {
        Text("Sin avisos todavía", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        avisos.take(20).forEach { aviso ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        aviso.origen.texto,
                        modifier = Modifier.background(colorOrigen(aviso.origen), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 5.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(aviso.titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(aviso.texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(aviso.hora, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

private fun colorOrigen(origen: Origen): Color = when (origen) {
    Origen.BOTON -> Color(0xFF1769AA)
    Origen.RECORDATORIO -> Color(0xFFB77900)
    Origen.MODO_AVION -> Color(0xFF7650A8)
    Origen.CARGADOR -> Color(0xFF137C83)
}
