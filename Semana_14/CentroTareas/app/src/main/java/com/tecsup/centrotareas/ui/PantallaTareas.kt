package com.tecsup.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val carga by vm.carga.collectAsStateWithLifecycle()
    val simularError by vm.simularError.collectAsStateWithLifecycle()
    var limite by remember { mutableStateOf("5000000") }

    Scaffold(topBar = { TopAppBar(title = { Text("CentroTareas") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Tarjeta("Tarea con progreso") {
                Text("Contador de toques: $toques", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = vm::tareaLarga, enabled = !corriendo) { Text("Iniciar") }
                    OutlinedButton(onClick = vm::cancelar, enabled = corriendo) { Text("Cancelar") }
                    OutlinedButton(onClick = vm::tocar) { Text("Tócame") }
                }
                LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth())
                Text("${(progreso * 100).toInt()} % · $mensaje")
            }

            Tarjeta("Contar números primos") {
                OutlinedTextField(
                    value = limite,
                    onValueChange = { valor -> if (valor.all(Char::isDigit)) limite = valor },
                    label = { Text("Límite superior") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { limite.toIntOrNull()?.let(vm::contar) },
                        enabled = primos != PrimosUi.Calculando && (limite.toIntOrNull() ?: 0) >= 2) { Text("Contar primos") }
                    OutlinedButton(onClick = vm::cancelarPrimos, enabled = primos == PrimosUi.Calculando) { Text("Cancelar") }
                }
                when (val estado = primos) {
                    PrimosUi.Inicial -> Text("Escribe un número igual o mayor que 2.")
                    PrimosUi.Calculando -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(); Text("Calculando en segundo plano...")
                    }
                    PrimosUi.Cancelado -> Text("Cálculo cancelado.")
                    is PrimosUi.Listo -> Text("Primos encontrados: ${estado.cantidad} · ${estado.milisegundos} ms")
                }
            }

            Tarjeta("Carga de información") {
                Text("Simula la carga de un perfil y sus pedidos.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = vm::cargarUnaPorUna, enabled = carga != CargaUi.Cargando) { Text("Secuencial") }
                    Button(onClick = vm::cargarALaVez, enabled = carga != CargaUi.Cargando) { Text("En paralelo") }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Simular error de conexión")
                    Switch(checked = simularError, onCheckedChange = vm::cambiarSimularError)
                }
                when (val estado = carga) {
                    CargaUi.Inicial -> Text("Compara el tiempo de carga de las dos opciones.")
                    CargaUi.Cargando -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(); Text("Cargando...")
                    }
                    is CargaUi.Exito -> Text("Modo: ${estado.modo}\nPerfil: ${estado.perfil}\nPedidos: ${estado.pedidos}\nTiempo: ${estado.milisegundos} ms")
                    is CargaUi.Error -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                        OutlinedButton(onClick = vm::reintentar) { Text("Reintentar") }
                    }
                }
            }
        }
    }
}

@Composable
private fun Tarjeta(titulo: String, contenido: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(titulo, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            contenido()
        }
    }
}

