package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle

@Composable
fun HistorialItem(detalle: CitaConDetalle) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("${detalle.cita.fecha} · ${detalle.servicio.nombre}", style = MaterialTheme.typography.titleMedium)
            Text(detalle.cita.diagnostico.orEmpty())
            if (!detalle.cita.tratamiento.isNullOrBlank()) Text("Tratamiento: ${detalle.cita.tratamiento}")
            detalle.cita.pesoRegistrado?.let { Text("Peso: $it kg") }
        }
    }
}
