package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.CitaCard
import com.tecsup.huellitasvet.ui.components.CalendarioMensual
import com.tecsup.huellitasvet.ui.components.FechaChip
import com.tecsup.huellitasvet.viewmodel.AgendaViewModel
import java.time.LocalDate

@Composable
fun AgendaScreen(vm: AgendaViewModel, onAtender: (Int) -> Unit) {
    val fecha by vm.fechaSeleccionada.collectAsStateWithLifecycle()
    val citas by vm.citas.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val mes by vm.mes.collectAsStateWithLifecycle()
    val calendario by vm.calendario.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        Text("Agenda clínica", Modifier.padding(20.dp), style = MaterialTheme.typography.headlineMedium)
        CalendarioMensual(mes, fecha, calendario, vm::moverMes, vm::seleccionarDia)
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items((0..7).map { LocalDate.now().plusDays(it.toLong()) }) { dia -> FechaChip(dia, fecha == dia.toString()) { vm.fechaSeleccionada.value = dia.toString() } }
        }
        LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (mensaje != null) item { Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error) }
            if (citas.isEmpty()) item { Text("No hay citas este día") }
            items(citas, key = { it.cita.id }) { detalle ->
                CitaCard(detalle) {
                    if (detalle.cita.estado == "PENDIENTE") TextButton(onClick = { vm.confirmar(detalle) }) { Text("Confirmar") }
                    if (detalle.cita.estado in listOf("PENDIENTE", "CONFIRMADA")) {
                        TextButton(onClick = { onAtender(detalle.cita.id) }) { Text("Atender") }
                        TextButton(onClick = { vm.cancelar(detalle) }) { Text("Cancelar") }
                    }
                }
            }
        }
    }
}
