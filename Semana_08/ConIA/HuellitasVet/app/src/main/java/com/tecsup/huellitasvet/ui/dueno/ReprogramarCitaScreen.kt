package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.model.Horarios
import com.tecsup.huellitasvet.ui.components.FechaChip
import com.tecsup.huellitasvet.ui.components.HoraChip
import com.tecsup.huellitasvet.viewmodel.ReprogramarCitaViewModel
import java.time.LocalDate

/** Solo permite cambiar fecha y hora: mascota, servicio y precio permanecen intactos. */
@Composable
fun ReprogramarCitaScreen(vm: ReprogramarCitaViewModel, onGuardada: () -> Unit) {
    val cita by vm.cita.collectAsStateWithLifecycle()
    val fecha by vm.fecha.collectAsStateWithLifecycle()
    val hora by vm.hora.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val ocupadas by vm.ocupadas.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Reprogramar cita", Modifier.padding(20.dp), style = MaterialTheme.typography.headlineMedium) }
        item { Text("${cita?.mascota?.mascota?.nombre.orEmpty()} · ${cita?.servicio?.nombre.orEmpty()}\nHorario actual: ${cita?.cita?.fecha.orEmpty()} · ${cita?.cita?.hora.orEmpty()}", Modifier.padding(horizontal = 20.dp)) }
        item { Text("Puedes cambiarla con más de 2 horas de anticipación.", Modifier.padding(horizontal = 20.dp)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((0..7).map { LocalDate.now().plusDays(it.toLong()) }) { dia ->
                    FechaChip(dia, fecha == dia.toString()) { vm.elegirFecha(dia.toString()) }
                }
            }
        }
        item {
            LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxWidth().height(320.dp).padding(horizontal = 20.dp)) {
                items(Horarios.todos) { valor ->
                    HoraChip(valor, hora == valor, valor in ocupadas || !vm.disponible(valor)) { vm.hora.value = valor }
                }
            }
        }
        if (mensaje != null) item { Text(mensaje.orEmpty(), Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error) }
        item { Button(onClick = { vm.guardar(onGuardada) }, enabled = cita != null && vm.disponible(hora), modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) { Text("Guardar nuevo horario") } }
    }
}
