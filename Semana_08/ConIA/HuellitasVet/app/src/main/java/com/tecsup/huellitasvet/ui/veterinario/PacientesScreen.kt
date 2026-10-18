package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.PacientesViewModel
import java.time.LocalDate

@Composable
fun PacientesScreen(vm: PacientesViewModel, onFicha: (Int) -> Unit) {
    val lista by vm.pacientes.collectAsStateWithLifecycle()
    val texto by vm.busqueda.collectAsStateWithLifecycle()
    val especie by vm.especie.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        Text("Pacientes", Modifier.padding(20.dp), style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(texto, { vm.busqueda.value = it }, label = { Text("Buscar mascota o dueño") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 20.dp)) {
            items(listOf("" to "Todos", "PERRO" to "Perros", "GATO" to "Gatos", "OTRO" to "Otros")) { (valor, titulo) ->
                FilterChip(especie == valor, { vm.especie.value = valor }, label = { Text(titulo) })
            }
        }
        LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (lista.isEmpty()) item { Text("No hay pacientes") }
            items(lista, key = { it.detalle.mascota.id }) { vista ->
                val mascota = vista.detalle.mascota
                Card(onClick = { onFicha(mascota.id) }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MascotaAvatar(mascota.especie)
                        Column {
                            Text(mascota.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${mascota.raza} · ${LocalDate.now().year - mascota.anioNacimiento} años")
                            Text("Dueño: ${vista.detalle.dueno.nombre}")
                            Text("Última visita: ${vista.ultimaVisita ?: "Sin visitas"}")
                        }
                    }
                }
            }
        }
    }
}
