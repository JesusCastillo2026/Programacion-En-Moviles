package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle

@Composable
fun HistorialItem(detalle: CitaConDetalle, modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(detalle.nombresServicios, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            Text("${detalle.cita.fecha} · ${detalle.cita.diagnostico.orEmpty()}", fontSize = 14.sp)
            if (!detalle.cita.tratamiento.isNullOrBlank()) Text("Tratamiento: ${detalle.cita.tratamiento}", fontSize = 12.sp)
            detalle.cita.pesoRegistrado?.let { Text("♥  Peso: $it kg", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary) }
        }
    }
}
