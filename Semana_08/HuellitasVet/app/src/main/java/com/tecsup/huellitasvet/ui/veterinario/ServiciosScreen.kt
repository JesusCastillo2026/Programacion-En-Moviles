package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel

@Composable
fun ServiciosScreen(vm: ServiciosViewModel, onEditar: (Int) -> Unit) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onEditar(0) }, containerColor = MaterialTheme.colorScheme.primary) {
            Text("+", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary)
        }
    }) { margen ->
        androidx.compose.foundation.lazy.LazyColumn(
            Modifier.fillMaxSize().padding(margen).padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (mensaje != null) item { Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error) }
            items(servicios, key = { it.id }) { servicio ->
                val icon = when (servicio.icono.lowercase()) {
                    "syringe", "vacuna" -> Icons.Default.Vaccines
                    "scissors", "corte" -> Icons.Default.ContentCut
                    "heart", "consulta" -> Icons.Default.Favorite
                    else -> Icons.Default.MedicalServices
                }
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(17.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 9.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(icon, null, Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(servicio.nombre, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                            Text(servicio.categorias.replace("|", " · "), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text("${servicio.duracionMin} min · S/ ${"%.2f".format(servicio.precio)}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = .82f))
                        }
                        Switch(servicio.activo, onCheckedChange = { vm.alternar(servicio) })
                        IconButton(onClick = { onEditar(servicio.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar ${servicio.nombre}", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
