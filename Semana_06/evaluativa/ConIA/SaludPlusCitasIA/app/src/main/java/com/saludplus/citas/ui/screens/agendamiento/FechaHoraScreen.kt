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
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TituloSeccion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FechaHoraScreen(medicoId: Int, onContinuar: (String, String) -> Unit) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val dias = remember { (1L..5L).map { LocalDate.now().plusDays(it) } }
    val formato = remember { DateTimeFormatter.ofPattern("EEE dd/MM", Locale.forLanguageTag("es-PE")) }
    var fecha by rememberSaveable(medicoId) { mutableStateOf("") }
    var hora by rememberSaveable(medicoId) { mutableStateOf("") }
    val horarios = Repositorio.horariosDisponibles(medicoId, fecha)
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        TituloSeccion("Elige fecha y hora")
        Text(medico?.nombre ?: "Médico")
        Text("Próximos días")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dias) { dia ->
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
