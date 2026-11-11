package com.tecsup.huellitasvet.ui.components.graficos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.DatoGrafico
import androidx.compose.foundation.background

@Composable
fun GraficoBarras(datos: List<DatoGrafico>) {
    val maximo = datos.maxOfOrNull { it.valor }?.coerceAtLeast(1f) ?: 1f
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        datos.forEach { dato ->
            Text("${dato.etiqueta} · ${dato.valor.toInt()}")
            Row(Modifier.fillMaxWidth().height(12.dp).background(MaterialTheme.colorScheme.primaryContainer)) {
                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth((dato.valor / maximo).coerceIn(0f, 1f)).height(12.dp).background(MaterialTheme.colorScheme.primary))
            }
        }
    }
}
