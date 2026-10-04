package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal

@Composable
fun CitaExitosaScreen(citaId: Int, onCitas: () -> Unit, onInicio: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("✓", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("¡Cita agendada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(Repositorio.obtenerMedico(cita?.medicoId ?: -1)?.nombre ?: "Médico")
        Text("${cita?.fecha.orEmpty()} · ${cita?.hora.orEmpty()}")
        Spacer(Modifier.height(32.dp))
        BotonPrincipal("Ver mis citas", onClick = onCitas)
        TextButton(onClick = onInicio) { Text("Volver al inicio") }
    }
}
