package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.HistorialItem
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.components.GraficoPeso
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel
import java.time.LocalDate

@Composable
fun FichaMascotaScreen(vm: MascotaViewModel, editable: Boolean, onEditar: () -> Unit, onPedir: () -> Unit, onEliminado: () -> Unit) {
    val mascota by vm.mascota.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val pesos by vm.pesos.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var pestana by remember { mutableIntStateOf(0) }
    var confirmar by remember { mutableStateOf(false) }
    val actual = mascota
    if (actual == null) { Text("Cargando mascota…", modifier = Modifier.padding(20.dp)); return }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MascotaAvatar(actual.especie, tamano = 100.dp)
            Text(actual.nombre, style = MaterialTheme.typography.headlineMedium)
            Text("${actual.especie.lowercase()} · ${actual.raza} · ${LocalDate.now().year - actual.anioNacimiento} años")
            Text("Peso actual: ${actual.peso} kg")
            if (editable) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onPedir) { Text("Pedir cita") }
                TextButton(onClick = onEditar) { Text("Editar") }
                TextButton(onClick = { confirmar = true }) { Text("Eliminar") }
            }
            if (mensaje != null) Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error)
        }
        TabRow(selectedTabIndex = pestana) {
            Tab(pestana == 0, onClick = { pestana = 0 }, text = { Text("Datos") })
            Tab(pestana == 1, onClick = { pestana = 1 }, text = { Text("Historial clínico") })
        }
        if (pestana == 0) Text("${actual.nombre} · ${actual.sexo.lowercase()} · ${actual.raza}", Modifier.padding(20.dp))
        else LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { GraficoPeso(pesos) }
            if (historial.isEmpty()) item { Text("Aún no tiene atenciones") }
            items(historial, key = { it.cita.id }) { HistorialItem(it) }
        }
    }
    if (confirmar) AlertDialog(onDismissRequest = { confirmar = false }, title = { Text("Eliminar mascota") }, text = { Text("¿Quieres eliminar a ${actual.nombre}?") }, confirmButton = { TextButton(onClick = { confirmar = false; vm.eliminar(onEliminado) }) { Text("Eliminar") } }, dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Volver") } })
}
