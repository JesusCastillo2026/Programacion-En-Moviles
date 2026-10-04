package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.proximosDiasHabiles
import com.saludplus.citas.data.tituloPeriodo
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TituloSeccion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Controla el calendario semanal, la fecha elegida y los turnos disponibles. */
@Composable
fun FechaHoraScreen(medicoId: Int, onContinuar: (String, String) -> Unit) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val hoy = remember { LocalDate.now() }
    var semana by rememberSaveable(medicoId) { mutableIntStateOf(0) }
    var fecha by rememberSaveable(medicoId) { mutableStateOf("") }
    var hora by rememberSaveable(medicoId) { mutableStateOf("") }
    val dias = remember(hoy, semana) { proximosDiasHabiles(hoy, semana) }
    val formato = remember { DateTimeFormatter.ofPattern("EEE dd", Locale.forLanguageTag("es-PE")) }
    val horarios = Repositorio.horariosDisponibles(medicoId, fecha)

    // Si otra cita ocupa el turno, la selección deja de ser válida.
    LaunchedEffect(horarios) {
        if (hora.isNotBlank() && hora !in horarios) hora = ""
    }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TituloSeccion("Elige fecha y hora")
        Text(medico?.nombre ?: "Médico")

        // Las flechas desplazan la vista una semana y bloquean el regreso antes de hoy.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = {
                semana--
                fecha = ""
                hora = ""
            }, enabled = semana > 0) { Text("‹", style = MaterialTheme.typography.headlineMedium) }
            Text(tituloPeriodo(dias), modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                semana++
                fecha = ""
                hora = ""
            }) { Text("›", style = MaterialTheme.typography.headlineMedium) }
        }

        // Al cambiar de día se reinicia la hora y se recalculan los horarios.
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dias, key = { it.toString() }) { dia ->
                val valor = dia.toString()
                FilterChip(selected = fecha == valor, onClick = {
                    fecha = valor
                    hora = ""
                }, label = { Text(dia.format(formato)) })
            }
        }
        Text("Horarios disponibles")
        if (fecha.isBlank()) {
            Text("Selecciona un día para ver los horarios.")
        } else if (horarios.isEmpty()) {
            Text("No hay horarios libres para este día.")
        } else {
            // El repositorio filtra las horas ya reservadas para este médico y fecha.
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(horarios) { opcion ->
                    FilterChip(selected = hora == opcion, onClick = { hora = opcion },
                        label = { Text(opcion) })
                }
            }
        }
        BotonPrincipal("Continuar", habilitado = fecha.isNotBlank() && hora.isNotBlank()) {
            onContinuar(fecha, hora)
        }
    }
}
