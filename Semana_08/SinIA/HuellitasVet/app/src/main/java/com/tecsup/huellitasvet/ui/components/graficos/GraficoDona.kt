package com.tecsup.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.DatoGrafico

private val colores = listOf(Color(0xFFD97706), Color(0xFF16A34A), Color(0xFF475569), Color(0xFFDC2626))

@Composable
fun GraficoDona(datos: List<DatoGrafico>) {
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()
    Column {
        Canvas(Modifier.fillMaxWidth().height(145.dp).padding(12.dp)) {
            var inicio = -90f
            if (total == 0f) drawArc(Color.LightGray, 0f, 360f, false, style = Stroke(24.dp.toPx()))
            datos.forEachIndexed { indice, dato ->
                val angulo = if (total > 0) 360f * dato.valor / total else 0f
                drawArc(colores[indice % colores.size], inicio, angulo, false, style = Stroke(24.dp.toPx()))
                inicio += angulo
            }
        }
        datos.forEachIndexed { indice, dato -> Text("● ${dato.etiqueta.lowercase()}: ${dato.valor.toInt()}", color = colores[indice % colores.size]) }
    }
}
