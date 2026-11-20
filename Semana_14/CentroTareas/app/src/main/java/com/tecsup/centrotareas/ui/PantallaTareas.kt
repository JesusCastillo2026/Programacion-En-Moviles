package com.tecsup.centrotareas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.viewmodel.TareasViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    val toques by vm.toques.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val progreso by vm.progreso.collectAsStateWithLifecycle()
    val corriendo by vm.corriendo.collectAsStateWithLifecycle()
    val primos by vm.primos.collectAsStateWithLifecycle()
    val progresoPrimos by vm.progresoPrimos.collectAsStateWithLifecycle()
    val carga by vm.carga.collectAsStateWithLifecycle()
    val simularError by vm.simularError.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val diferencia by vm.diferencia.collectAsStateWithLifecycle()
    var limite by remember { mutableStateOf("5000000") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CENTRO DE OPERACIONES", fontSize = 10.sp, letterSpacing = 1.6.sp,
                            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("CentroTareas", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF17343A))
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("LABORATORIO 14 · HILOS", fontSize = 11.sp, letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Trabajo fluido, incluso en segundo plano", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, lineHeight = 31.sp)
                    Text("Explora concurrencia, cancelación y cargas concurrentes en una sola pantalla.",
                        color = Color(0xFFC5D3D7), style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        Text(if (corriendo) "Tarea activa" else "Sistema listo", fontSize = 12.sp, color = Color(0xFFD5E3E4))
                    }
                }
            }

            Tarjeta("01", "Una tarea que no bloquea") {
                Text("Interactúa con la pantalla mientras el trabajo avanza.", color = Color(0xFFB8C5CE))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = vm::tareaLarga, enabled = !corriendo) { Text("Iniciar tarea") }
                    OutlinedButton(onClick = vm::cancelar, enabled = corriendo) { Text("Cancelar") }
                }
                LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary, trackColor = Color(0xFF34434D))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(mensaje, color = Color(0xFFE1EAED))
                    Text("${(progreso * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = vm::tocar, modifier = Modifier.fillMaxWidth()) {
                    Text("Tócame · contador $toques")
                }
            }

            Tarjeta("02", "Búsqueda de números primos") {
                Text("El cálculo se ejecuta fuera del hilo principal y se puede cancelar.", color = Color(0xFFB8C5CE))
                OutlinedTextField(
                    value = limite,
                    onValueChange = { value -> if (value.all(Char::isDigit)) limite = value },
                    label = { Text("Límite superior") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { limite.toIntOrNull()?.let(vm::contar) },
                        enabled = primos != PrimosUi.Calculando && (limite.toIntOrNull() ?: 0) >= 2) { Text("Calcular") }
                    OutlinedButton(onClick = vm::cancelarPrimos, enabled = primos == PrimosUi.Calculando) { Text("Detener") }
                }
                when (val estado = primos) {
                    PrimosUi.Inicial -> Text("Introduce un límite igual o mayor que 2.", color = Color(0xFFB8C5CE))
                    PrimosUi.Calculando, is PrimosUi.Progreso -> Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        LinearProgressIndicator(progresoPrimos, modifier = Modifier.fillMaxWidth())
                        Text("${(progresoPrimos * 100).toInt()}% · cálculo en segundo plano", color = Color(0xFFB8C5CE))
                    }
                    PrimosUi.Cancelado -> Text("El cálculo se canceló.", color = MaterialTheme.colorScheme.tertiary)
                    is PrimosUi.Listo -> Resultado("${estado.cantidad} números primos", "Hasta $limite · ${estado.milisegundos} ms")
                }
            }

            Tarjeta("03", "Dos cargas, dos estrategias") {
                Text("Compara cargar el perfil y los pedidos en secuencia o al mismo tiempo.", color = Color(0xFFB8C5CE))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = vm::cargarUnaPorUna, enabled = carga != CargaUi.Cargando) { Text("Secuencial") }
                    Button(onClick = vm::cargarALaVez, enabled = carga != CargaUi.Cargando) { Text("Paralela") }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Simular fallo de red")
                    Switch(checked = simularError, onCheckedChange = vm::cambiarSimularError)
                }
                when (val estado = carga) {
                    CargaUi.Inicial -> Text("Los datos de perfil y pedidos son de demostración.", color = Color(0xFFB8C5CE))
                    CargaUi.Cargando -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp); Text("Solicitando información...")
                    }
                    is CargaUi.Exito -> Resultado("Carga ${estado.modo.lowercase()}", "${estado.perfil} · ${estado.pedidos} pedidos · ${estado.milisegundos} ms")
                    is CargaUi.Error -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                        OutlinedButton(onClick = vm::reintentar) { Text("Reintentar") }
                    }
                }
                diferencia?.let { (secuencial, paralelo) ->
                    Spacer(Modifier.height(2.dp))
                    Text("Comparación guardada · secuencial ${secuencial} ms · paralela ${paralelo} ms",
                        color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }

            Tarjeta("04", "Actividad reciente") {
                if (historial.isEmpty()) Text("Aquí aparecerán tus últimos cálculos y cargas.", color = Color(0xFFB8C5CE))
                historial.forEach { item ->
                    Text("${item.categoria}  ·  ${item.resumen}", fontWeight = FontWeight.SemiBold)
                    Text("${item.milisegundos} ms", color = Color(0xFFB8C5CE), fontSize = 12.sp)
                }
            }
            Text("Los datos de perfil y pedidos son simulados · CentroTareas", modifier = Modifier.padding(bottom = 22.dp),
                color = Color(0xFF80909C), fontSize = 11.sp)
        }
    }
}

@Composable
private fun Tarjeta(numero: String, titulo: String, contenido: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151F29)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(32.dp).background(Color(0xFF203C43), CircleShape), contentAlignment = Alignment.Center) {
                    Text(numero, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            contenido()
        }
    }
}

@Composable
private fun Resultado(titulo: String, detalle: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF203C43))) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(titulo, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Text(detalle, fontSize = 12.sp, color = Color(0xFFD4E0E3))
        }
    }
}

