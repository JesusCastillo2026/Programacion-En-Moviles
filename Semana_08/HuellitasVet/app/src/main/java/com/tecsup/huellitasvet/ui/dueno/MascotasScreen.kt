package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.InicioViewModel
import java.time.LocalDate

@Composable
fun MascotasScreen(vm: InicioViewModel, onMascota: (Int) -> Unit, onAgregar: () -> Unit) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    Scaffold(floatingActionButton = { FloatingActionButton(onClick = onAgregar) { Text("+") } }) { margen ->
        LazyColumn(Modifier.fillMaxSize().padding(margen).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Mis mascotas", style = MaterialTheme.typography.headlineMedium) }
            if (mascotas.isEmpty()) item { Text("Aún no registras mascotas") }
            items(mascotas, key = { it.id }) { mascota ->
                Card(Modifier.fillMaxWidth().clickable { onMascota(mascota.id) }) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MascotaAvatar(mascota.especie)
                        Column {
                            Text(mascota.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${mascota.especie.lowercase()} · ${mascota.raza} · ${LocalDate.now().year - mascota.anioNacimiento} años")
                        }
                    }
                }
            }
        }
    }
}
