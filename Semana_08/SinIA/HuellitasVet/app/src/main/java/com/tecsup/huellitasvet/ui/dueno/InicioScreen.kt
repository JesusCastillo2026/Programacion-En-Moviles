package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.InicioViewModel

@Composable
fun InicioScreen(nombre: String, vm: InicioViewModel, onMascota: (Int) -> Unit, onAgregar: () -> Unit, onPedir: () -> Unit) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val proxima by vm.proximaCita.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Column(Modifier.padding(20.dp)) {
                Text("Hola, $nombre 👋", style = MaterialTheme.typography.headlineMedium)
                Text("Tu familia peluda está en buenas manos", color = MaterialTheme.colorScheme.primary)
            }
        }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Próxima cita", style = MaterialTheme.typography.titleLarge)
                    if (proxima == null) Text("Aún no tienes citas")
                    else {
                        Text("${proxima!!.mascota.mascota.nombre} · ${proxima!!.servicio.nombre}")
                        Text("${proxima!!.cita.fecha} a las ${proxima!!.cita.hora}")
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mis mascotas", style = MaterialTheme.typography.titleLarge)
                androidx.compose.material3.TextButton(onClick = onAgregar) { Text("+ Agregar") }
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)) {
                items(mascotas, key = { it.id }) { mascota ->
                    Card(Modifier.clickable { onMascota(mascota.id) }) {
                        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            MascotaAvatar(mascota.especie)
                            Text(mascota.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(mascota.especie.lowercase())
                        }
                    }
                }
            }
        }
        item { Button(onClick = onPedir, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) { Text("Pedir una cita") } }
    }
}
