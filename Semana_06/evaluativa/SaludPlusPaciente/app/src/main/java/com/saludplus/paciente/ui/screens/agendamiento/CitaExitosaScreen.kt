package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.components.SoftCard
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.CelesteSuave
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CitaExitosaScreen(citaId: String, onHome: () -> Unit, onAppointments: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    val especialidad = cita?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }
    val locale = Locale("es", "PE")

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = AzulClinico, modifier = Modifier.clip(CircleShape).background(CelesteSuave).padding(20.dp))
        Spacer(Modifier.height(20.dp))
        Text("¡Cita agendada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = AzulClinico)
        Text("Guardamos los detalles en Mis citas.", textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        if (cita != null) {
            SoftCard {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(medico?.nombre ?: "Profesional", fontWeight = FontWeight.Bold)
                    Text(especialidad?.nombre ?: "Consulta")
                    Text(cita.fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", locale)).replaceFirstChar { it.uppercase(locale) })
                    Text("${cita.hora} · Presencial")
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        SaludPlusButton("Ver mis citas", onAppointments)
        Spacer(Modifier.height(10.dp))
        androidx.compose.material3.TextButton(onClick = onHome) { Text("Volver al inicio") }
    }
}

