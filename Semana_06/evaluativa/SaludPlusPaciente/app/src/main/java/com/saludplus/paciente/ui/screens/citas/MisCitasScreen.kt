package com.saludplus.paciente.ui.screens.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.model.Cita
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.EmptyState
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.components.SoftCard
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
/** Lista las citas del paciente y enlaza cada tarjeta con su detalle. */
fun MisCitasScreen(onDetails: (String) -> Unit, onNewAppointment: () -> Unit) {
    val citas = Repositorio.citasDelUsuario()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { PageHeading("Mis citas", "Consulta los detalles y organiza tus atenciones") }
        if (citas.isEmpty()) {
            item {
                EmptyState("Todavía no tienes citas", "Elige una especialidad y reserva tu primera atención.",
                    actionLabel = "Agendar una cita", onAction = onNewAppointment)
            }
        } else {
                items(citas, key = { it.id }) { cita ->
                    CitaCard(cita, onClick = { onDetails(cita.id) })
                }
            item { SaludPlusButton("Agendar otra cita", onNewAppointment, modifier = Modifier.padding(vertical = 12.dp)) }
        }
    }
}

@Composable
private fun CitaCard(cita: Cita, onClick: () -> Unit) {
    val medico = Repositorio.obtenerMedico(cita.medicoId)
    val especialidad = Repositorio.obtenerEspecialidad(cita.especialidadId)
    val fecha = cita.fecha.format(DateTimeFormatter.ofPattern("EEE d 'de' MMMM", Locale("es", "PE")))
    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AzulClinico)
                Column(modifier = Modifier.weight(1f)) {
                    Text(especialidad?.nombre ?: "Consulta", fontWeight = FontWeight.Bold)
                    Text(medico?.nombre ?: "Profesional", color = TextoSecundario)
                }
            }
            Text(cita.estado.name.lowercase().replaceFirstChar { it.uppercase() }, color = AzulClinico, fontWeight = FontWeight.SemiBold)
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(fecha.replaceFirstChar { it.uppercase(Locale("es", "PE")) }, color = TextoSecundario)
                Text(cita.hora, color = AzulClinico, fontWeight = FontWeight.Bold)
            }
            androidx.compose.material3.TextButton(onClick = onClick, modifier = Modifier.align(Alignment.End)) { Text("Ver detalle") }
        }
    }
}

