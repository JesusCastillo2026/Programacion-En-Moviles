package com.saludplus.paciente.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Progreso de cuatro pasos; las etiquetas se distribuyen en varias líneas si falta ancho. */
@Composable
fun ReservaProgress(paso: Int) {
    val nombres = listOf("Especialidad", "Médico", "Horario", "Confirmación")
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Paso $paso de 4 · ${nombres[paso - 1]}", style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.semantics { heading() })
        LinearProgressIndicator(progress = { paso / 4f }, modifier = Modifier.fillMaxWidth())
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            nombres.forEachIndexed { index, nombre ->
                Text("${index + 1}. $nombre", style = MaterialTheme.typography.labelSmall,
                    color = if (index + 1 == paso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
