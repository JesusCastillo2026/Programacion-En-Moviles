package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.PuntoPeso
import java.util.Locale

/** Dibuja la evolución clínica sin acoplar la vista a Room ni al repositorio. */
@Composable
fun GraficoPeso(puntos: List<PuntoPeso>) {
    val tinta = MaterialTheme.colorScheme.primary
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Evolución del peso", style = MaterialTheme.typography.titleMedium)
            if (puntos.isEmpty()) {
                Text("El gráfico aparecerá después de registrar una atención con peso.")
            } else {
                val minimo = puntos.minOf { it.peso }
                val maximo = puntos.maxOf { it.peso }
                Text("${String.format(Locale.US, "%.1f", minimo)}–${String.format(Locale.US, "%.1f", maximo)} kg · ${puntos.size} medición(es)")
                Canvas(Modifier.fillMaxWidth().height(150.dp).padding(top = 12.dp)) {
                    val margen = 12.dp.toPx()
                    val ancho = size.width - margen * 2
                    val alto = size.height - margen * 2
                    val rango = (maximo - minimo).coerceAtLeast(0.5)
                    val posiciones = puntos.mapIndexed { indice, punto ->
                        val x = margen + if (puntos.size == 1) ancho / 2 else ancho * indice / (puntos.size - 1)
                        val y = margen + alto * (1 - ((punto.peso - minimo) / rango).toFloat())
                        Offset(x, y)
                    }
                    drawLine(tinta.copy(alpha = 0.25f), Offset(margen, size.height - margen), Offset(size.width - margen, size.height - margen))
                    if (posiciones.size > 1) {
                        val camino = Path().apply {
                            moveTo(posiciones.first().x, posiciones.first().y)
                            posiciones.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(camino, tinta, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()))
                    }
                    posiciones.forEach { drawCircle(tinta, radius = 5.dp.toPx(), center = it) }
                }
                Text("${puntos.first().fecha}  →  ${puntos.last().fecha}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
