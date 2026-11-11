package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.ui.components.CalendarioMensual
import com.tecsup.huellitasvet.ui.components.EstadoCitaChip
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.AgendaViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AgendaScreen(vm: AgendaViewModel, onSeleccionarCita: (Int) -> Unit) {
    val fecha by vm.fechaSeleccionada.collectAsStateWithLifecycle()
    val citas by vm.citas.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val mes by vm.mes.collectAsStateWithLifecycle()
    val calendario by vm.calendario.collectAsStateWithLifecycle()
    var mostrarMes by remember { mutableStateOf(false) }
    val diaSeleccionado = runCatching { LocalDate.parse(fecha) }.getOrDefault(LocalDate.now())

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 6.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${citas.size} citas · ${diaSeleccionado.dayOfMonth} ${diaSeleccionado.month.getDisplayName(TextStyle.SHORT, Locale("es", "PE")).replace(".", "")}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { mostrarMes = !mostrarMes }) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario mensual", tint = MaterialTheme.colorScheme.primary)
            }
        }
        if (mostrarMes) CalendarioMensual(mes, fecha, calendario, vm::moverMes, vm::seleccionarDia)

        val dias = (0L..3L).map { diaSeleccionado.plusDays(it) }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(dias) { dia ->
                val etiqueta = if (dia == LocalDate.now()) "Hoy ${dia.dayOfMonth}" else {
                    "${dia.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "PE")).replace(".", "").replaceFirstChar { it.uppercase() }} ${dia.dayOfMonth}"
                }
                FilterChip(
                    selected = fecha == dia.toString(),
                    onClick = { vm.seleccionarDia(dia) },
                    label = { Text(etiqueta, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${diaSeleccionado.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "PE")).replaceFirstChar { it.uppercase() }} ${diaSeleccionado.dayOfMonth}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${citas.size} ${if (citas.size == 1) "cita" else "citas"}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (mensaje != null) item { Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error) }
            if (citas.isEmpty()) item {
                Card(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("No hay citas para este día.", Modifier.padding(18.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(citas, key = { it.cita.id }) { detalle ->
                CitaEnLinea(detalle) { onSeleccionarCita(detalle.cita.id) }
            }
        }
    }
}

@Composable
private fun CitaEnLinea(detalle: CitaConDetalle, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(
            Modifier.width(46.dp).height(104.dp).padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(detalle.cita.hora, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
            Box(Modifier.padding(top = 4.dp).width(2.dp).weight(1f).background(MaterialTheme.colorScheme.primaryContainer))
        }
        TarjetaCitaAgenda(detalle, onClick, Modifier.weight(1f))
    }
}

@Composable
private fun TarjetaCitaAgenda(detalle: CitaConDetalle, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MascotaAvatar(detalle.mascota.mascota.especie, tamano = 42.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    "${detalle.mascota.mascota.nombre} · ${detalle.nombresServicios}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text("Dueño: ${detalle.mascota.dueno.nombre}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            EstadoCitaChip(detalle.cita.estado)
        }
    }
}
