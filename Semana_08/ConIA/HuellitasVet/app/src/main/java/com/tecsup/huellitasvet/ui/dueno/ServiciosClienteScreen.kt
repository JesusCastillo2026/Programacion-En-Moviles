package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.PedirCitaViewModel

@Composable
fun ServiciosClienteScreen(
    vm: PedirCitaViewModel,
    onAgendar: (mascotaId: Int, serviciosIds: List<Int>) -> Unit,
    onRegistrarMascota: () -> Unit
) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    var mascotaId by remember { mutableStateOf(0) }
    val serviciosSeleccionadosIds = remember { mutableStateListOf<Int>() }
    val categoriasElegidas = remember { mutableStateListOf<String>() }
    val categorias = servicios.flatMap { servicio ->
        servicio.categorias.split("|").map { it.trim() }.filter { it.isNotEmpty() }
    }.distinct().sorted()
    val serviciosVisibles = servicios.filter { servicio ->
        val tipos = servicio.categorias.split("|").map { it.trim() }
        categoriasElegidas.isEmpty() || tipos.any { it in categoriasElegidas }
    }
    val mascotaElegida = mascotas.find { it.id == mascotaId }
    val serviciosElegidos = servicios.filter { it.id in serviciosSeleccionadosIds }

    LaunchedEffect(mascotas) {
        if (mascotas.none { it.id == mascotaId }) mascotaId = mascotas.firstOrNull()?.id ?: 0
    }
    LaunchedEffect(servicios.map { it.id }) {
        serviciosSeleccionadosIds.retainAll(servicios.map { it.id }.toSet())
    }

    val coloresCategoria = FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .55f),
        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    )

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Servicios", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Elige a tu mascota y encuentra la atención que necesita.", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = .82f))
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("¿Qué mascota recibirá la atención?", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    if (mascotas.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text("Primero registra una mascota para continuar.", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 14.sp)
                            }
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(mascotas, key = { it.id }) { mascota ->
                                FilterChip(
                                    selected = mascota.id == mascotaId,
                                    onClick = { mascotaId = mascota.id },
                                    label = { Text(mascota.nombre) },
                                    leadingIcon = { MascotaAvatar(mascota.especie, tamano = 25.dp) },
                                    colors = coloresCategoria
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("Tipo de servicio", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text("Puedes marcar varias categorías para filtrar el catálogo.", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = .82f))
                    if (categorias.isEmpty()) {
                        Text("Aún no hay categorías disponibles.", color = MaterialTheme.colorScheme.primary)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            items(categorias, key = { it }) { categoria ->
                                FilterChip(
                                    selected = categoria in categoriasElegidas,
                                    onClick = {
                                        if (categoria in categoriasElegidas) categoriasElegidas.remove(categoria)
                                        else categoriasElegidas.add(categoria)
                                    },
                                    label = { Text(categoria) },
                                    colors = coloresCategoria
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Atenciones disponibles", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text("Marca una o varias para esta cita.", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = .82f))
                }
            }

            if (serviciosVisibles.isEmpty()) {
                item {
                    Text("No hay servicios para las categorías seleccionadas.", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 10.dp))
                }
            } else {
                items(serviciosVisibles, key = { it.id }) { servicio ->
                    val seleccionado = servicio.id in serviciosSeleccionadosIds
                    Card(
                        onClick = {
                            if (!serviciosSeleccionadosIds.remove(servicio.id)) serviciosSeleccionadosIds.add(servicio.id)
                        },
                        modifier = Modifier.fillMaxWidth().then(
                            if (seleccionado) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)) else Modifier
                        ),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = when (servicio.icono.lowercase()) {
                                    "syringe", "vacuna" -> Icons.Default.Vaccines
                                    "scissors", "corte" -> Icons.Default.ContentCut
                                    "heart", "consulta" -> Icons.Default.Favorite
                                    else -> Icons.Default.MedicalServices
                                },
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(servicio.nombre, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text(servicio.categorias.replace("|", " · "), color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                Text("${servicio.duracionMin} min · S/ ${"%.2f".format(servicio.precio)}", color = MaterialTheme.colorScheme.primary.copy(alpha = .82f), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            if (serviciosElegidos.isNotEmpty() && mascotaElegida != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            "${mascotaElegida.nombre} · ${serviciosElegidos.joinToString(" · ") { it.nombre }}\n" +
                                "${serviciosElegidos.sumOf { it.duracionMin }} min · S/ ${"%.2f".format(serviciosElegidos.sumOf { it.precio })}",
                            Modifier.fillMaxWidth().padding(13.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (mascotas.isEmpty()) onRegistrarMascota()
                else if (mascotaId > 0 && serviciosSeleccionadosIds.isNotEmpty()) onAgendar(mascotaId, serviciosSeleccionadosIds.toList())
            },
            enabled = mascotas.isEmpty() || (mascotaId > 0 && serviciosSeleccionadosIds.isNotEmpty()),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp).height(50.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(if (mascotas.isEmpty()) "Registrar una mascota" else "Continuar con la cita", fontWeight = FontWeight.SemiBold)
        }
    }
}
