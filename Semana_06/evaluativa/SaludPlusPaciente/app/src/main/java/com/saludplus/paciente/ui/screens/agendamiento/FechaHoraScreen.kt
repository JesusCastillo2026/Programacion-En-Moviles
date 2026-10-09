package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.model.BorradorReserva
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.*
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Un único contenedor desplazable adapta los turnos al ancho y al tamaño de letra. */
@Composable
fun FechaHoraScreen(
    sedeId: String,
    especialidadId: String,
    medicoId: String,
    onBack: () -> Unit,
    onContinue: (String, String) -> Unit,
    borrador: BorradorReserva = rememberSaveable(saver = BorradorReserva.saver) { BorradorReserva() }
) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val ahora by recordarHoraActual()
    val hoy = ahora.toLocalDate()
    val semanaActual = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val inicioSemana = runCatching { LocalDate.parse(borrador.semana) }.getOrDefault(semanaActual).coerceAtLeast(semanaActual)
    val dias = remember(inicioSemana, hoy) { Repositorio.cincoDiasHabiles(inicioSemana) }
    val fecha = runCatching { LocalDate.parse(borrador.fecha) }.getOrNull()
    val horarios = fecha?.let { Repositorio.horariosDisponibles(sedeId, medicoId, it, ahora) }.orEmpty()
    val locale = Locale.forLanguageTag("es-PE")
    val fontScale = LocalDensity.current.fontScale
    val puedeContinuar = fecha in dias && borrador.hora in horarios &&
        Repositorio.validarReserva(sedeId, especialidadId, medicoId, fecha, borrador.hora, ahora) == null
    LaunchedEffect(especialidadId, medicoId) {
        borrador.elegirEspecialidad(especialidadId)
        borrador.elegirMedico(medicoId)
    }

    Column(Modifier.fillMaxSize()) {
        AppBackTopBar("Fecha y hora", onBack)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(maxOf(96.dp, 72.dp * fontScale)),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    ReservaProgress(4)
                    PageHeading("Elige cuándo", medico?.let { "${it.nombre} · ${it.especialidadNombre}" } ?: "Selecciona un profesional")
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val mes = dias.first().format(DateTimeFormatter.ofPattern("MMMM yyyy", locale))
                    Text(mes.replaceFirstChar { it.uppercase(locale) }, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { borrador.cambiarSemana(inicioSemana.minusWeeks(1).toString()) },
                        enabled = inicioSemana.isAfter(semanaActual)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Semana anterior")
                    }
                    IconButton(onClick = { borrador.cambiarSemana(inicioSemana.plusWeeks(1).toString()) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Semana siguiente")
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dias, key = { it.toString() }) { dia ->
                        val elegido = dia == fecha
                        Surface(
                            color = if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.widthIn(min = maxOf(64.dp, 50.dp * fontScale))
                                .selectable(selected = elegido, role = Role.RadioButton,
                                    onClick = { borrador.elegirFecha(dia.toString()) })
                                .semantics { contentDescription = dia.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy", locale)) }
                        ) {
                            Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(dia.format(DateTimeFormatter.ofPattern("EEE", locale)), style = MaterialTheme.typography.labelLarge)
                                Text(dia.dayOfMonth.toString(), style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Horarios disponibles", style = MaterialTheme.typography.titleMedium)
            }
            if (fecha == null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Selecciona un día para ver sus turnos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (horarios.isEmpty() || fecha !in dias) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState("Elige otro día", "Esta fecha no tiene turnos disponibles. Puedes cambiar de día o avanzar una semana.",
                        actionLabel = "Semana siguiente",
                        onAction = { borrador.cambiarSemana(inicioSemana.plusWeeks(1).toString()) })
                }
            } else {
                items(horarios, key = { it }) { hora ->
                    val elegido = borrador.hora == hora
                    Surface(
                        color = if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                            .selectable(selected = elegido, role = Role.RadioButton, onClick = { borrador.hora = hora })
                    ) {
                        Box(Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                            Text(hora, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (borrador.hora.isNotEmpty() && borrador.hora !in horarios) {
                        FormError("El turno que elegiste ya no está disponible. Selecciona otro horario.")
                    }
                    SaludPlusButton("Revisar mi cita", onClick = {
                        if (Repositorio.validarReserva(sedeId, especialidadId, medicoId, fecha, borrador.hora) == null) {
                            onContinue(borrador.fecha, borrador.hora)
                        }
                    },
                        enabled = puedeContinuar)
                    Text("Puedes volver al paso anterior: conservaremos tu selección.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

