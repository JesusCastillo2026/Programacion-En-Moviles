package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.ui.components.CitaCard
import com.tecsup.huellitasvet.viewmodel.MisCitasViewModel

@Composable
fun MisCitasScreen(vm: MisCitasViewModel, onPedir: () -> Unit, onReprogramar: (Int) -> Unit) {
    val proximas by vm.proximas.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var pestana by remember { mutableIntStateOf(0) }
    var seleccion by remember { mutableStateOf<CitaConDetalle?>(null) }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, floatingActionButton = { FloatingActionButton(onClick = onPedir, containerColor = MaterialTheme.colorScheme.primary) { Text("+") } }) { margen ->
        androidx.compose.foundation.layout.Column(Modifier.fillMaxSize().padding(margen)) {
            Text("Mis citas", Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 10.dp), fontSize = 22.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            TabRow(pestana, containerColor = MaterialTheme.colorScheme.background) {
                Tab(pestana == 0, { pestana = 0 }, text = { Text("Próximas") })
                Tab(pestana == 1, { pestana = 1 }, text = { Text("Historial") })
            }
            val lista = if (pestana == 0) proximas else historial
            LazyColumn(Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (mensaje != null) item { Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error) }
                if (pestana == 0) item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFF2C6))) {
                        Text("Puedes cancelar o reprogramar hasta 2 horas antes", Modifier.padding(11.dp), style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color(0xFF744600))
                    }
                }
                if (lista.isEmpty()) item { Text("No hay citas para mostrar", Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(lista, key = { it.cita.id }) { detalle ->
                    CitaCard(detalle) {
                        if (vm.puedeCancelar(detalle)) Row {
                            TextButton(onClick = { onReprogramar(detalle.cita.id) }) { Text("Cambiar", fontSize = 12.sp) }
                            TextButton(onClick = { seleccion = detalle }) { Text("Cancelar", color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                        }
                    }
                }
            }
        }
    }
    seleccion?.let { detalle ->
        AlertDialog(onDismissRequest = { seleccion = null }, title = { Text("Cancelar cita") }, text = { Text("¿Cancelar la cita de ${detalle.mascota.mascota.nombre}?") }, confirmButton = { TextButton(onClick = { vm.cancelar(detalle); seleccion = null }) { Text("Sí, cancelar") } }, dismissButton = { TextButton(onClick = { seleccion = null }) { Text("Volver") } })
    }
}
