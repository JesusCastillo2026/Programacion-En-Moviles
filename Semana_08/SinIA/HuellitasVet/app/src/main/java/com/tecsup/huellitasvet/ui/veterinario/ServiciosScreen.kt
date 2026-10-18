package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel

@Composable
fun ServiciosScreen(vm: ServiciosViewModel, onEditar: (Int) -> Unit) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    Scaffold(floatingActionButton = { FloatingActionButton(onClick = { onEditar(0) }) { Text("+") } }) { margen ->
        LazyColumn(Modifier.fillMaxSize().padding(margen).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Servicios", style = MaterialTheme.typography.headlineMedium) }
            if (mensaje != null) item { Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error) }
            items(servicios, key = { it.id }) { servicio ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(servicio.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("${servicio.duracionMin} min · S/ ${servicio.precio}")
                        Text(servicio.descripcion)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Activo")
                            Switch(servicio.activo, onCheckedChange = { vm.alternar(servicio) })
                            TextButton(onClick = { onEditar(servicio.id) }) { Text("Editar") }
                            TextButton(onClick = { vm.eliminar(servicio) }) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}
