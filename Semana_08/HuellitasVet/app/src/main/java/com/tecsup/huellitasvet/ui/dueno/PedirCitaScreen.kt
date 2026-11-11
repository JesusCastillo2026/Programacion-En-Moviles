package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.model.Horarios
import com.tecsup.huellitasvet.ui.components.FechaChip
import com.tecsup.huellitasvet.ui.components.HoraChip
import com.tecsup.huellitasvet.viewmodel.PedirCitaViewModel
import java.time.LocalDate

@Composable
fun PedirCitaScreen(vm: PedirCitaViewModel, onConfirmada: () -> Unit) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val fecha by vm.fecha.collectAsStateWithLifecycle()
    val mascotaId by vm.mascotaId.collectAsStateWithLifecycle()
    val servicioId by vm.servicioId.collectAsStateWithLifecycle()
    val hora by vm.hora.collectAsStateWithLifecycle()
    val nota by vm.nota.collectAsStateWithLifecycle()
    val ocupadas by vm.horasOcupadas.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val acepta by vm.aceptarCitas.collectAsStateWithLifecycle()
    val servicio = servicios.find { it.id == servicioId }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Pedir una cita", Modifier.padding(20.dp), style = MaterialTheme.typography.headlineMedium) }
        if (!acepta) item { Text("La clínica no está recibiendo citas", Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error) }
        item { Text("1. Elige tu mascota", Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.titleMedium) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(mascotas, key = { it.id }) { mascota -> FilterChip(mascotaId == mascota.id, { vm.mascotaId.value = mascota.id }, label = { Text(mascota.nombre) }) }
            }
        }
        item { Text("2. Servicio", Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.titleMedium) }
        items(servicios, key = { it.id }) { item ->
            Card(onClick = { vm.servicioId.value = item.id }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) { Text(item.nombre, style = MaterialTheme.typography.titleMedium); Text("${item.duracionMin} min · ${item.descripcion}") }
                    Text("S/ ${item.precio}")
                }
            }
        }
        item { Text("3. Fecha", Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.titleMedium) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((0..7).map { LocalDate.now().plusDays(it.toLong()) }) { dia -> FechaChip(dia, fecha == dia.toString()) { vm.elegirFecha(dia.toString()) } }
            }
        }
        item { Text("4. Hora disponible", Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.titleMedium) }
        item {
            LazyVerticalGrid(GridCells.Fixed(3), modifier = Modifier.fillMaxWidth().height(320.dp).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Horarios.todos) { valor -> HoraChip(valor, hora == valor, valor in ocupadas || !vm.horaDisponible(valor)) { vm.hora.value = valor } }
            }
        }
        item { OutlinedTextField(nota, { vm.nota.value = it }, label = { Text("Nota (opcional)") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.titleMedium)
                    Text("${mascotas.find { it.id == mascotaId }?.nombre ?: "Mascota"} · ${servicio?.nombre ?: "Servicio"}")
                    Text("$fecha · ${hora.ifEmpty { "Elige hora" }} · S/ ${servicio?.precio ?: 0.0}")
                }
            }
        }
        if (mensaje != null) item { Text(mensaje.orEmpty(), Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error) }
        item { Button(onClick = { vm.confirmar(onConfirmada) }, enabled = acepta && mascotaId > 0 && servicioId > 0 && vm.horaDisponible(hora), modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) { Text("Confirmar cita") } }
    }
}
