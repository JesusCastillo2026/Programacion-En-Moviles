package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.model.Horarios
import com.tecsup.huellitasvet.ui.components.FechaChip
import com.tecsup.huellitasvet.ui.components.HoraChip
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.PedirCitaViewModel
import java.time.LocalDate

@Composable
fun PedirCitaScreen(vm: PedirCitaViewModel, onConfirmada: () -> Unit) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val fecha by vm.fecha.collectAsStateWithLifecycle()
    val mascotaId by vm.mascotaId.collectAsStateWithLifecycle()
    val servicioId by vm.servicioId.collectAsStateWithLifecycle()
    val hora by vm.hora.collectAsStateWithLifecycle()
    val nota by vm.nota.collectAsStateWithLifecycle()
    val ocupadas by vm.horasOcupadas.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val acepta by vm.aceptarCitas.collectAsStateWithLifecycle()
    val mascotaElegida = mascotas.find { it.id == mascotaId }
    val servicio = servicios.find { it.id == servicioId }

    LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (!acepta) item { Text("La clínica no está recibiendo citas", Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("¿Quién viene?", Modifier.padding(horizontal = 20.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(mascotas, key = { it.id }) { mascota ->
                        FilterChip(
                            selected = mascotaId == mascota.id,
                            onClick = { vm.mascotaId.value = mascota.id },
                            label = { Text(mascota.nombre) },
                            leadingIcon = { MascotaAvatar(mascota.especie, tamano = 24.dp) }
                        )
                    }
                }
            }
        }
        item { Text("Servicio", Modifier.padding(horizontal = 20.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        items(servicios, key = { it.id }) { item ->
            val seleccionado = item.id == servicioId
            Card(
                onClick = { vm.servicioId.value = item.id },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).then(if (seleccionado) Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)) else Modifier),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.padding(horizontal = 13.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    androidx.compose.material3.Icon(
                        when (item.icono) { "syringe" -> Icons.Default.Vaccines; "scissors" -> Icons.Default.Pets; else -> Icons.Default.Favorite },
                        null, tint = MaterialTheme.colorScheme.primary
                    )
                    Column(Modifier.weight(1f)) {
                        Text(item.nombre, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("${item.duracionMin} min", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("S/ ${item.precio.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
        item {
            Text("Fecha", Modifier.padding(horizontal = 20.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items((0..4).map { LocalDate.now().plusDays(it.toLong()) }) { dia -> FechaChip(dia, fecha == dia.toString()) { vm.elegirFecha(dia.toString()) } }
            }
        }
        item { Text("Hora", Modifier.padding(horizontal = 20.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        item {
            LazyVerticalGrid(
                GridCells.Fixed(3), modifier = Modifier.fillMaxWidth().height(206.dp).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(Horarios.todos) { valor ->
                    HoraChip(valor, hora == valor, valor in ocupadas || !vm.horaDisponible(valor)) { vm.hora.value = valor }
                }
            }
        }
        if (nota.isNotBlank()) item { Text("Nota: $nota", Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.bodySmall) }
        if (mensaje != null) item { Text(mensaje.orEmpty(), Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error) }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${mascotaElegida?.nombre ?: "Mascota"} · ${servicio?.nombre ?: "Servicio"} · ${hora.ifEmpty { "Elige hora" }}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }
        item {
            Button(
                onClick = { vm.confirmar(onConfirmada) }, enabled = acepta && mascotaId > 0 && servicioId > 0 && vm.horaDisponible(hora),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(48.dp),
                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Confirmar cita", fontWeight = FontWeight.SemiBold) }
        }
    }
}
