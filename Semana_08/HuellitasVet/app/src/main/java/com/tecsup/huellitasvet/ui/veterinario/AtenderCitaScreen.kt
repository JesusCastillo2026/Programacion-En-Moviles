package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.AtenderCitaViewModel

@Composable
fun AtenderCitaScreen(vm: AtenderCitaViewModel, onTerminar: () -> Unit) {
    val detalle by vm.cita.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var peso by remember { mutableStateOf("") }
    var diagnostico by remember { mutableStateOf("") }
    var tratamiento by remember { mutableStateOf("") }
    LaunchedEffect(detalle?.cita?.id) { detalle?.let { peso = it.mascota.mascota.peso.toString() } }
    val actual = detalle
    if (actual == null) { Text("Cargando cita…", Modifier.padding(20.dp)); return }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Atender cita", style = MaterialTheme.typography.headlineMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MascotaAvatar(actual.mascota.mascota.especie)
            Column { Text(actual.mascota.mascota.nombre, style = MaterialTheme.typography.titleLarge); Text("${actual.mascota.dueno.nombre} · ${actual.servicio.nombre}"); Text("${actual.cita.fecha} · ${actual.cita.hora}") }
        }
        OutlinedTextField(peso, { peso = it }, label = { Text("Peso actual (kg)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(diagnostico, { diagnostico = it }, label = { Text("Diagnóstico") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        OutlinedTextField(tratamiento, { tratamiento = it }, label = { Text("Tratamiento") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Text("Se actualizará el peso de ${actual.mascota.mascota.nombre}")
        if (mensaje != null) Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error)
        Button(onClick = { vm.registrar(diagnostico, tratamiento, peso, onTerminar) }, modifier = Modifier.fillMaxWidth()) { Text("Registrar atención") }
        TextButton(onClick = { vm.cancelar(onTerminar) }) { Text("Cancelar cita") }
    }
}
