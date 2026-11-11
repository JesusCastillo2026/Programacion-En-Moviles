package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.graficos.GraficoBarras
import com.tecsup.huellitasvet.ui.components.graficos.GraficoDona
import com.tecsup.huellitasvet.viewmodel.PanelViewModel

@Composable
fun PanelScreen(vm: PanelViewModel, onAgenda: () -> Unit) {
    val hoy by vm.citasHoy.collectAsStateWithLifecycle()
    val pendientes by vm.pendientes.collectAsStateWithLifecycle()
    val atendidas by vm.atendidas.collectAsStateWithLifecycle()
    val ingresos by vm.ingresosMes.collectAsStateWithLifecycle()
    val estados by vm.porEstado.collectAsStateWithLifecycle()
    val servicios by vm.porServicio.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Panel de la clínica", style = MaterialTheme.typography.headlineMedium) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Hoy" to hoy, "Pendientes" to pendientes, "Atendidas" to atendidas).forEach { (titulo, valor) ->
                    Card(Modifier.weight(1f)) { Column(Modifier.padding(12.dp)) { Text(valor.toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary); Text(titulo) } }
                }
            }
        }
        item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp)) { Text("Ingresos del mes"); Text("S/ $ingresos", style = MaterialTheme.typography.headlineMedium) } } }
        if (pendientes > 0) item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Tienes $pendientes citas por confirmar"); Button(onClick = onAgenda) { Text("Ver agenda") } } } }
        item { Text("Citas por estado", style = MaterialTheme.typography.titleLarge); GraficoDona(estados) }
        item { Text("Citas por servicio", style = MaterialTheme.typography.titleLarge); GraficoBarras(servicios) }
    }
}
