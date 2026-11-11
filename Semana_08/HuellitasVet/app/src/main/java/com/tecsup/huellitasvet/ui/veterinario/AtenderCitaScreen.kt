package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.tecsup.huellitasvet.ui.components.EstadoCitaChip
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.AtenderCitaViewModel

@Composable
fun AtenderCitaScreen(vm: AtenderCitaViewModel, onTerminar: () -> Unit) {
    val detalle by vm.cita.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var peso by remember { mutableStateOf("") }
    var diagnostico by remember { mutableStateOf("") }
    var tratamiento by remember { mutableStateOf("") }
    var confirmarCancelacion by remember { mutableStateOf(false) }
    LaunchedEffect(detalle?.cita?.id) { detalle?.let { peso = it.mascota.mascota.peso.toString() } }

    val actual = detalle
    if (actual == null) {
        Text("Cargando cita…", Modifier.padding(20.dp))
        return
    }
    val estado = actual.cita.estado
    val pendiente = estado == "PENDIENTE"
    val confirmada = estado == "CONFIRMADA"
    val abierta = pendiente || confirmada

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MascotaAvatar(actual.mascota.mascota.especie, tamano = 54.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(actual.mascota.mascota.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${actual.mascota.mascota.especie.lowercase().replaceFirstChar { it.uppercase() }} · ${actual.mascota.dueno.nombre}", fontSize = 14.sp)
                    Text("${actual.nombresServicios} · ${actual.cita.fecha} · ${actual.cita.hora}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
                EstadoCitaChip(estado)
            }
        }

        if (pendiente) {
            Text("Confirma la cita para habilitar el registro clínico.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                androidx.compose.material3.Icon(Icons.Default.Favorite, null, Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Column {
                Text("Registro clínico", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("Peso, diagnóstico y tratamiento", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (abierta) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso actual (kg)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    enabled = confirmada
                )
                Card(
                    Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 13.dp)) {
                        Text("♥  Peso anterior", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Text("${actual.mascota.mascota.peso} kg", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            OutlinedTextField(
                value = diagnostico,
                onValueChange = { diagnostico = it },
                label = { Text("Diagnóstico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 2,
                enabled = confirmada
            )
            OutlinedTextField(
                value = tratamiento,
                onValueChange = { tratamiento = it },
                label = { Text("Tratamiento") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 2,
                enabled = confirmada
            )
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .55f)) {
                Text(
                    "✓  El peso se actualizará en la ficha de ${actual.mascota.mascota.nombre}.",
                    Modifier.fillMaxWidth().padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(
                    if (estado == "ATENDIDA") "La atención ya fue registrada." else "Esta cita fue cancelada y ya no admite cambios.",
                    Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (mensaje != null) Text(mensaje.orEmpty(), color = if (mensaje == "Cita confirmada") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)

        when {
            pendiente -> Button(
                onClick = { vm.confirmar() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp)
        ) { Text("Confirmar cita", fontSize = 14.sp) }
            confirmada -> Button(
                onClick = { vm.registrar(diagnostico, tratamiento, peso, onTerminar) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp)
            ) {
                androidx.compose.material3.Icon(Icons.Default.MedicalServices, null)
                Text("  Registrar atención", fontSize = 14.sp)
            }
        }
        if (abierta) OutlinedButton(
            onClick = { confirmarCancelacion = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp)
        ) { Text("Cancelar cita", color = MaterialTheme.colorScheme.error, fontSize = 14.sp) }
    }

    if (confirmarCancelacion) AlertDialog(
        onDismissRequest = { confirmarCancelacion = false },
        title = { Text("Cancelar cita") },
        text = { Text("¿Deseas cancelar la cita de ${actual.mascota.mascota.nombre}?") },
        confirmButton = {
            TextButton(onClick = { confirmarCancelacion = false; vm.cancelar(onTerminar) }) {
                Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = { confirmarCancelacion = false }) { Text("Volver") } }
    )
}
