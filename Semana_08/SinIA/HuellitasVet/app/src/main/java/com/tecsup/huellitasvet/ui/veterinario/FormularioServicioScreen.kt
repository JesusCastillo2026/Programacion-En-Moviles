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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel

@Composable
fun FormularioServicioScreen(vm: ServiciosViewModel, id: Int, onGuardado: () -> Unit) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf(30) }
    var precio by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("heart") }
    var activo by remember { mutableStateOf(true) }
    LaunchedEffect(id, servicios) {
        servicios.find { it.id == id }?.let { nombre = it.nombre; descripcion = it.descripcion; duracion = it.duracionMin; precio = it.precio.toString(); icono = it.icono; activo = it.activo }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(if (id == 0) "Nuevo servicio" else "Editar servicio", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(descripcion, { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Text("Duración")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(15, 30, 45, 60).forEach { valor -> FilterChip(duracion == valor, { duracion = valor }, label = { Text("$valor") }) } }
        OutlinedTextField(precio, { precio = it }, label = { Text("Precio (S/)") }, modifier = Modifier.fillMaxWidth())
        Text("Ícono")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("heart", "syringe", "scissors").forEach { valor -> FilterChip(icono == valor, { icono = valor }, label = { Text(valor) }) } }
        Row { Text("Servicio activo"); Switch(activo, { activo = it }) }
        if (mensaje != null) Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error)
        Button(onClick = { vm.guardar(Servicio(id, nombre.trim(), descripcion.trim(), duracion, precio.replace(',', '.').toDoubleOrNull() ?: 0.0, activo, icono), onGuardado) }, modifier = Modifier.fillMaxWidth()) { Text("Guardar servicio") }
    }
}
