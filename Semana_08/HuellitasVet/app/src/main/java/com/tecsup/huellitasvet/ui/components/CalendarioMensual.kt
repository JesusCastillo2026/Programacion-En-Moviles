package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.DiaAgenda
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/** Calendario de lunes a domingo: cada celda muestra las citas no canceladas de ese día. */
@Composable
fun CalendarioMensual(mes: YearMonth, seleccion: String, dias: List<DiaAgenda>, onMes: (Long) -> Unit, onDia: (LocalDate) -> Unit) {
    val conteos = dias.associate { it.fecha to it.cantidad }
    val desplazamiento = mes.atDay(1).dayOfWeek.value - 1
    val total = ((desplazamiento + mes.lengthOfMonth() + 6) / 7) * 7
    Card(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onMes(-1) }) { Text("‹ Anterior") }
                Text("${mes.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-PE")).replaceFirstChar { it.uppercase() }} ${mes.year}", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { onMes(1) }) { Text("Siguiente ›") }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEach { Text(it, Modifier.weight(1f), textAlign = TextAlign.Center) }
            }
            repeat(total / 7) { semana ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(7) { columna ->
                        val numero = semana * 7 + columna - desplazamiento + 1
                        if (numero in 1..mes.lengthOfMonth()) {
                            val fecha = mes.atDay(numero)
                            OutlinedButton(onClick = { onDia(fecha) }, modifier = Modifier.weight(1f).padding(1.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                                Text(if ((conteos[fecha.toString()] ?: 0) > 0) "$numero•" else "$numero", color = if (seleccion == fecha.toString()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            }
                        } else {
                            Text("", Modifier.weight(1f))
                        }
                    }
                }
            }
            Text("• Días con citas · Toca un día para ver su agenda", style = MaterialTheme.typography.bodySmall)
        }
    }
}
