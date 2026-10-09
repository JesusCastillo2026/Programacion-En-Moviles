package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.CelesteSuave
import com.saludplus.paciente.ui.theme.TextoSecundario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FechaHoraScreen(
    especialidadId: String,
    medicoId: String,
    onBack: () -> Unit,
    onContinue: (String, String) -> Unit
) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val diasDisponibles = remember { (1L..5L).map { LocalDate.now().plusDays(it) } }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var horaSeleccionada by remember { mutableStateOf<String?>(null) }
    val horarios = fechaSeleccionada?.let { Repositorio.horariosDisponibles(medicoId, it) }.orEmpty()
    val locale = Locale("es", "PE")

    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Seleccionar fecha y hora", onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            PageHeading("Elige cuándo", medico?.let { "${it.nombre} · ${it.especialidadNombre}" } ?: "Selecciona una cita disponible")
            Spacer(Modifier.height(18.dp))
            Text("FECHA", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextoSecundario)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                diasDisponibles.forEach { dia ->
                    val seleccionado = fechaSeleccionada == dia
                    Card(
                        modifier = Modifier.weight(1f).clickable {
                            fechaSeleccionada = dia
                            horaSeleccionada = null
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (seleccionado) AzulClinico else Color.White)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(dia.format(DateTimeFormatter.ofPattern("EEE", locale)).replaceFirstChar { it.uppercase(locale) }, color = if (seleccionado) Color.White else TextoSecundario, style = MaterialTheme.typography.labelSmall)
                            Text(dia.dayOfMonth.toString(), color = if (seleccionado) Color.White else AzulClinico, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            Text("HORARIOS DISPONIBLES", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextoSecundario)
            Spacer(Modifier.height(10.dp))
            if (fechaSeleccionada == null) {
                Text("Primero selecciona un día.", color = TextoSecundario)
            } else if (horarios.isEmpty()) {
                Text("No hay horarios disponibles para este día.", color = TextoSecundario)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(horarios, key = { it }) { hora ->
                        val seleccionado = horaSeleccionada == hora
                        Text(
                            text = hora,
                            modifier = Modifier.fillMaxWidth().background(
                                if (seleccionado) AzulClinico else Color.White,
                                RoundedCornerShape(12.dp)
                            ).clickable { horaSeleccionada = hora }.padding(vertical = 13.dp),
                            color = if (seleccionado) Color.White else AzulClinico,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            SaludPlusButton(
                text = "Continuar",
                onClick = {
                    val fecha = fechaSeleccionada
                    val hora = horaSeleccionada
                    if (fecha != null && hora != null) onContinue(fecha.toString(), hora)
                },
                enabled = fechaSeleccionada != null && horaSeleccionada != null
            )
            Spacer(Modifier.height(18.dp))
        }
    }
}

