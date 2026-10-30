package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.EncabezadoSeccion
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.InicioViewModel

@Composable
fun InicioScreen(nombre: String, vm: InicioViewModel, onMascota: (Int) -> Unit, onAgregar: () -> Unit, onPedir: () -> Unit, onVerMascotas: () -> Unit = {}) {
    var mostrarAviso by remember { mutableStateOf(false) }
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val proxima by vm.proximaCita.collectAsStateWithLifecycle()
    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Hola, $nombre", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("¿Cómo están tus mascotas hoy?", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                IconButton(onClick = { mostrarAviso = true }) { Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = MaterialTheme.colorScheme.primary) }
                }
            }
        }
        item {
            val cita = proxima
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Próxima cita", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .78f), fontSize = 12.sp)
                        Text(cita?.let { "${it.mascota.mascota.nombre} · ${it.servicio.nombre}" } ?: "Cuida su salud con una revisión", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(15.dp))
                            Text(cita?.let { "${it.cita.fecha} · ${it.cita.hora}" } ?: "Agenda una visita cuando la necesites", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp)
                        }
                        Surface(shape = RoundedCornerShape(30.dp), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .18f)) {
                            Text(cita?.cita?.estado?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Atención veterinaria", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    if (cita != null) MascotaAvatar(cita.mascota.mascota.especie, tamano = 52.dp)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Mis mascotas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Toca una tarjeta para ver su ficha", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onVerMascotas) { Text("Ver todas", fontSize = 12.sp) }
            }
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(mascotas, key = { it.id }) { mascota ->
                    Card(
                        Modifier.size(width = 104.dp, height = 112.dp).clickable { onMascota(mascota.id) },
                        shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            MascotaAvatar(mascota.especie, tamano = 54.dp)
                            Text(mascota.nombre, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                            Text(mascota.raza, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                }
                item {
                    Card(
                        Modifier.size(width = 104.dp, height = 112.dp).clickable(onClick = onAgregar),
                        shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface) { Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(10.dp)) }
                            Text("Agregar", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        item { Text("Servicios", Modifier.padding(start = 20.dp, end = 20.dp, top = 2.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(servicios, key = { it.id }) { servicio ->
                    Card(onClick = onPedir, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.size(width = 100.dp, height = 82.dp).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(
                                when (servicio.icono) {
                                    "syringe" -> Icons.Default.Vaccines
                                    "scissors" -> Icons.Default.Pets
                                    else -> Icons.Default.Favorite
                                }, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)
                            )
                            Text(servicio.nombre, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, maxLines = 1)
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFF2C6))) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Schedule, null, tint = androidx.compose.ui.graphics.Color(0xFFB56B00))
                    Column {
                        Text("Recordatorio", fontWeight = FontWeight.SemiBold, color = androidx.compose.ui.graphics.Color(0xFF744600), fontSize = 14.sp)
                        Text("Mantén al día sus vacunas y controles", fontSize = 12.sp, color = androidx.compose.ui.graphics.Color(0xFF744600))
                    }
                }
            }
        }
        item {
            Button(onClick = onPedir, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Pedir una cita", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
    if (mostrarAviso) AlertDialog(
        onDismissRequest = { mostrarAviso = false },
        title = { Text("Notificaciones") },
        text = { Text("Aquí aparecerán los recordatorios y avisos de tus citas.") },
        confirmButton = { TextButton(onClick = { mostrarAviso = false }) { Text("Entendido") } }
    )
}
