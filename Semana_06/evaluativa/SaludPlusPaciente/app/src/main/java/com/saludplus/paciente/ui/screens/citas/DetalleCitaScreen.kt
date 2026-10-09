package com.saludplus.paciente.ui.screens.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.SoftCard
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
/** Expone el resumen de una reserva y una confirmación antes de cancelarla. */
fun DetalleCitaScreen(citaId: String, onBack: () -> Unit, onCancelled: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    var pedirConfirmacion by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Detalle de cita", onBack)
        if (cita == null) {
            Text("No encontramos esta cita.", modifier = Modifier.padding(24.dp))
        } else {
            val medico = Repositorio.obtenerMedico(cita.medicoId)
            val especialidad = Repositorio.obtenerEspecialidad(cita.especialidadId)
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Información de tu cita", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                SoftCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetalleLinea("Profesional", medico?.nombre ?: "—")
                        DetalleLinea("Especialidad", especialidad?.nombre ?: "—")
                        DetalleLinea("Fecha", cita.fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale("es", "PE"))))
                        DetalleLinea("Hora", cita.hora)
                        DetalleLinea("Motivo", cita.motivo.ifBlank { "No especificado" })
                        DetalleLinea("Estado", cita.estado.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { pedirConfirmacion = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar cita", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (pedirConfirmacion) {
        AlertDialog(
            onDismissRequest = { pedirConfirmacion = false },
            title = { Text("¿Cancelar esta cita?") },
            text = { Text("La cita se quitará de tu lista y el horario volverá a estar disponible.") },
            confirmButton = {
                TextButton(onClick = {
                    Repositorio.cancelarCita(citaId)
                    pedirConfirmacion = false
                    onCancelled()
                }) { Text("Sí, cancelar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { pedirConfirmacion = false }) { Text("Conservar") } }
        )
    }
}

@Composable
private fun DetalleLinea(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = TextoSecundario, style = MaterialTheme.typography.labelMedium)
        Text(value, color = AzulClinico, fontWeight = FontWeight.SemiBold)
    }
}

