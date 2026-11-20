package com.tecsup.centrotareas.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.tecsup.centrotareas.model.EventoActividad
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.viewmodel.TareasViewModel
import kotlin.math.abs

private val TextoSecundario = Color(0xFFB8C5CE)
private val FondoTarjeta = Color(0xFF151F29)
private val FondoResultado = Color(0xFF203C43)

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
    val actividad by vm.actividad.collectAsStateWithLifecycle()
    val diferencia by vm.diferencia.collectAsStateWithLifecycle()
    var limite by remember { mutableStateOf("5000000") }

    val estadoTarea = when {
        corriendo -> "En curso"
        mensaje == "Terminó" -> "Completado"
        mensaje == "Cancelado" -> "Cancelado"
        else -> "En espera"
    }
    val estadoPrimos = when (primos) {
        PrimosUi.Calculando, is PrimosUi.Progreso -> "En curso"
        PrimosUi.Cancelado -> "Cancelado"
        is PrimosUi.Listo -> "Completado"
        PrimosUi.Inicial -> "En espera"
    }
    val estadoCarga = when (carga) {
        CargaUi.Cargando -> "En curso"
        is CargaUi.Exito -> "Completado"
        is CargaUi.Error -> "Error"
        CargaUi.Inicial -> "En espera"
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            "LABORATORIO 14 · CONCURRENCIA",
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text("CentroTareas", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF17343A))
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("CENTRO DE OPERACIONES", fontSize = 11.sp, letterSpacing = 1.4.sp,
                            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Trabajo fluido,\nincluso en segundo plano",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 31.sp
                    )
                    Text(
                        "Observa cómo avanzan las tareas mientras la pantalla sigue respondiendo.",
                        color = Color(0xFFC5D3D7),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        EstadoPill(if (corriendo || primos == PrimosUi.Calculando || carga == CargaUi.Cargando) "En curso" else "Sistema listo")
                        Text("3 ejercicios · progreso independiente", color = Color(0xFFD5E3E4), fontSize = 11.sp)
                    }
                }
            }

            Tarjeta("01", "Tarea larga y cancelación", estadoTarea) {
                Text("Inicia el trabajo, observa su avance y toca el contador para comprobar que la interfaz no se congela.", color = TextoSecundario)
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Button(onClick = vm::tareaLarga, enabled = !corriendo, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                        Spacer(Modifier.size(5.dp))
                        Text("Iniciar")
                    }
                    OutlinedButton(onClick = vm::cancelar, enabled = corriendo, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                }
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color(0xFF34434D)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(mensaje, color = Color(0xFFE1EAED))
                    Text("${(progreso * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = vm::tocar, modifier = Modifier.fillMaxWidth()) {
                    Text("Probar respuesta · contador $toques")
                }
            }

            Tarjeta("02", "Cálculo de números primos", estadoPrimos) {
                Text("El cálculo usa un hilo de trabajo y se puede detener sin bloquear la pantalla.", color = TextoSecundario)
                OutlinedTextField(
                    value = limite,
                    onValueChange = { value -> if (value.all(Char::isDigit)) limite = value },
                    label = { Text("Límite superior") },
                    supportingText = { Text("Escribe un número igual o mayor que 2") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Button(
                        onClick = { limite.toIntOrNull()?.let(vm::contar) },
                        enabled = primos != PrimosUi.Calculando && (limite.toIntOrNull() ?: 0) >= 2,
                        modifier = Modifier.weight(1f)
                    ) { Text("Calcular") }
                    OutlinedButton(
                        onClick = vm::cancelarPrimos,
                        enabled = primos == PrimosUi.Calculando,
                        modifier = Modifier.weight(1f)
                    ) { Text("Detener") }
                }
                when (val estado = primos) {
                    PrimosUi.Inicial -> Text("El resultado aparecerá aquí al terminar.", color = TextoSecundario)
                    PrimosUi.Calculando, is PrimosUi.Progreso -> Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        LinearProgressIndicator(progress = { progresoPrimos }, modifier = Modifier.fillMaxWidth())
                        Text("${(progresoPrimos * 100).toInt()}% · cálculo en segundo plano", color = TextoSecundario)
                    }
                    PrimosUi.Cancelado -> Resultado("Cálculo cancelado", "Puedes cambiar el límite e intentarlo otra vez.", MaterialTheme.colorScheme.tertiary)
                    is PrimosUi.Listo -> Resultado("${estado.cantidad} números primos", "Hasta $limite · ${estado.milisegundos} ms")
                }
            }

            Tarjeta("03", "Comparar estrategias de carga", estadoCarga) {
                Text("La carga secuencial espera cada resultado. La paralela solicita el perfil y los pedidos al mismo tiempo.", color = TextoSecundario)
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedButton(onClick = vm::cargarUnaPorUna, enabled = carga != CargaUi.Cargando, modifier = Modifier.weight(1f)) {
                        Text("Secuencial")
                    }
                    Button(onClick = vm::cargarALaVez, enabled = carga != CargaUi.Cargando, modifier = Modifier.weight(1f)) {
                        Text("En paralelo")
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Simular fallo de red", fontWeight = FontWeight.SemiBold)
                        Text("Prueba el estado de error y recuperación", color = TextoSecundario, fontSize = 11.sp)
                    }
                    Switch(checked = simularError, onCheckedChange = vm::cambiarSimularError)
                }
                when (val estado = carga) {
                    CargaUi.Inicial -> Resultado("Datos de demostración", "Perfil y pedidos son ficticios; no se conecta a un servicio real.")
                    CargaUi.Cargando -> Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text("Solicitando información…", color = TextoSecundario)
                        }
                    }
                    is CargaUi.Exito -> Resultado("Carga ${estado.modo.lowercase()}", "${estado.perfil} · ${estado.pedidos} pedidos · ${estado.milisegundos} ms")
                    is CargaUi.Error -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Resultado("No se completó la carga", estado.mensaje, MaterialTheme.colorScheme.error)
                        OutlinedButton(onClick = vm::reintentar, modifier = Modifier.fillMaxWidth()) { Text("Reintentar en paralelo") }
                    }
                }
                diferencia?.let { (secuencial, paralelo) ->
                    val antes = abs(secuencial - paralelo)
                    val texto = when {
                        secuencial > paralelo -> "La carga paralela terminó ${antes} ms antes."
                        paralelo > secuencial -> "La carga secuencial terminó ${antes} ms antes."
                        else -> "Ambas estrategias tardaron lo mismo."
                    }
                    Resultado("Comparación de tiempos", "Secuencial $secuencial ms · paralela $paralelo ms. $texto")
                }
            }

            Tarjeta("04", "Actividad reciente", if (actividad.isEmpty()) "En espera" else "${actividad.size} eventos") {
                if (actividad.isEmpty()) {
                    Text("Aquí verás cuándo empieza una tarea, cómo avanza y qué resultado obtiene.", color = TextoSecundario)
                } else {
                    actividad.take(6).forEachIndexed { indice, evento ->
                        EventoFila(evento, indice == 0)
                    }
                }
                if (historial.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text("RESULTADOS GUARDADOS", color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold)
                    historial.take(3).forEach { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.categoria} · ${item.resumen}", modifier = Modifier.weight(1f), color = Color(0xFFE1EAED), fontSize = 12.sp)
                            Text("${item.milisegundos} ms", color = TextoSecundario, fontSize = 11.sp)
                        }
                    }
                }
            }

            Text(
                "Las cargas de perfil y pedidos son simuladas · CentroTareas",
                modifier = Modifier.padding(bottom = 24.dp),
                color = Color(0xFF80909C),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun Tarjeta(numero: String, titulo: String, estado: String, contenido: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(34.dp).background(Color(0xFF203C43), CircleShape), contentAlignment = Alignment.Center) {
                    Text(numero, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(titulo, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                EstadoPill(estado)
            }
            contenido()
        }
    }
}

@Composable
private fun EstadoPill(texto: String) {
    val colorBase = when (texto) {
        "En curso" -> MaterialTheme.colorScheme.primary
        "Completado" -> Color(0xFF81D4A4)
        "Cancelado" -> MaterialTheme.colorScheme.tertiary
        "Error" -> MaterialTheme.colorScheme.error
        else -> Color(0xFF91A2AE)
    }
    val color by animateColorAsState(colorBase, label = "color del estado")
    Row(
        modifier = Modifier.background(color.copy(alpha = 0.13f), RoundedCornerShape(50)).padding(horizontal = 9.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).background(color, CircleShape))
        Text(texto, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EventoFila(evento: EventoActividad, reciente: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF111A22), RoundedCornerShape(14.dp)).padding(11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier.size(28.dp).background(if (reciente) FondoResultado else Color(0xFF202D37), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (reciente) Icons.Rounded.Timeline else Icons.Rounded.History,
                contentDescription = null,
                tint = if (reciente) MaterialTheme.colorScheme.primary else TextoSecundario,
                modifier = Modifier.size(15.dp)
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(evento.titulo, color = Color(0xFFE1EAED), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(evento.detalle, color = TextoSecundario, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun Resultado(titulo: String, detalle: String, colorTitulo: Color = MaterialTheme.colorScheme.primary) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = FondoResultado)) {
        Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = colorTitulo, modifier = Modifier.size(16.dp))
                Text(titulo, fontWeight = FontWeight.SemiBold, color = colorTitulo, fontSize = 13.sp)
            }
            Text(detalle, fontSize = 12.sp, color = Color(0xFFD4E0E3), lineHeight = 17.sp)
        }
    }
}
