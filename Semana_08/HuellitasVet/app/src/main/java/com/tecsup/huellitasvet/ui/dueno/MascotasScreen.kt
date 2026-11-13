package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.EncabezadoSeccion
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.InicioViewModel
import java.time.LocalDate

@Composable
fun MascotasScreen(vm: InicioViewModel, onMascota: (Int) -> Unit, onAgregar: () -> Unit) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = { FloatingActionButton(onClick = onAgregar, containerColor = MaterialTheme.colorScheme.primary) { Text("+") } }
    ) { margen ->
        LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(margen), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { EncabezadoSeccion("Mis mascotas", "Sus fichas, cuidados e historial") }
            if (mascotas.isEmpty()) item { Text("Aún no registras mascotas", Modifier.padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(mascotas, key = { it.id }) { mascota ->
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable { onMascota(mascota.id) },
                    shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MascotaAvatar(mascota.especie, tamano = 58.dp)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(mascota.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${mascota.especie.lowercase().replaceFirstChar { it.uppercase() }} · ${mascota.raza}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${LocalDate.now().year - mascota.anioNacimiento} años · ${mascota.peso} kg", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Abrir ficha", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
