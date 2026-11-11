package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle

@Composable
fun CitaCard(detalle: CitaConDetalle, acciones: @Composable () -> Unit = {}) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MascotaAvatar(detalle.mascota.mascota.especie)
                Column(Modifier.weight(1f)) {
                    Text(detalle.mascota.mascota.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(detalle.servicio.nombre)
                    Text("${detalle.cita.fecha} · ${detalle.cita.hora}")
                }
                EstadoCitaChip(detalle.cita.estado)
            }
            Text("${detalle.mascota.dueno.nombre} · S/ ${detalle.cita.precio}")
            acciones()
        }
    }
}
