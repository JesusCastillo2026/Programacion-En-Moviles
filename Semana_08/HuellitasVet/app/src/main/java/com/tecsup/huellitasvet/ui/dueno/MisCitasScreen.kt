package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.ui.components.CitaCard
import com.tecsup.huellitasvet.viewmodel.MisCitasViewModel

@Composable
fun MisCitasScreen(vm: MisCitasViewModel, onPedir: () -> Unit) {
    val proximas by vm.proximas.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var pestana by remember { mutableIntStateOf(0) }
    var seleccion by remember { mutableStateOf<CitaConDetalle?>(null) }
    Scaffold(floatingActionButton = { FloatingActionButton(onClick = onPedir) { Text("+") } }) { margen ->
        androidx.compose.foundation.layout.Column(Modifier.fillMaxSize().padding(margen)) {
            Text("Mis citas", Modifier.padding(20.dp), style = MaterialTheme.typography.headlineMedium)
            TabRow(pestana) {
                Tab(pestana == 0, { pestana = 0 }, text = { Text("Próximas") })
                Tab(pestana == 1, { pestana = 1 }, text = { Text("Historial") })
            }
            val lista = if (pestana == 0) proximas else historial
            LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (mensaje != null) item { Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error) }
                if (pestana == 0) item { Text("Puedes cancelar hasta 2 horas antes") }
                if (lista.isEmpty()) item { Text("No hay citas para mostrar") }
                items(lista, key = { it.cita.id }) { detalle ->
                    CitaCard(detalle) {
                        if (vm.puedeCancelar(detalle)) TextButton(onClick = { seleccion = detalle }) { Text("Cancelar") }
                    }
                }
            }
        }
    }
    seleccion?.let { detalle ->
        AlertDialog(onDismissRequest = { seleccion = null }, title = { Text("Cancelar cita") }, text = { Text("¿Cancelar la cita de ${detalle.mascota.mascota.nombre}?") }, confirmButton = { TextButton(onClick = { vm.cancelar(detalle); seleccion = null }) { Text("Sí, cancelar") } }, dismissButton = { TextButton(onClick = { seleccion = null }) { Text("Volver") } })
    }
}
