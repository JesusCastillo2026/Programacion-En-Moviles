package com.tecsup.huellitasvet.ui.dueno

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel

@Composable
fun FormularioMascotaScreen(vm: MascotaViewModel, onGuardado: () -> Unit) {
    val mascota by vm.mascota.collectAsStateWithLifecycle()
    val errores by vm.errores.collectAsStateWithLifecycle()
    var nombre by rememberSaveable { mutableStateOf("") }
    var especie by rememberSaveable { mutableStateOf("") }
    var raza by rememberSaveable { mutableStateOf("") }
    var anio by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(mascota?.id) {
        mascota?.let { nombre = it.nombre; especie = it.especie; raza = it.raza; anio = it.anioNacimiento.toString(); peso = it.peso.toString(); sexo = it.sexo }
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(if (vm.mascotaId == 0) "Nueva mascota" else "Editar mascota", style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { MascotaAvatar(especie, tamano = 100.dp) }
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, isError = errores.containsKey("nombre"), supportingText = { errores["nombre"]?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
        Text("Especie")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("PERRO", "GATO", "OTRO").forEach { valor -> FilterChip(especie == valor, { especie = valor }, label = { Text(valor.lowercase()) }) } }
        errores["especie"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(raza, { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(anio, { anio = it }, label = { Text("Año de nacimiento") }, isError = errores.containsKey("anio"), supportingText = { errores["anio"]?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(peso, { peso = it }, label = { Text("Peso (kg)") }, isError = errores.containsKey("peso"), supportingText = { errores["peso"]?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
        Text("Sexo")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("MACHO", "HEMBRA").forEach { valor -> FilterChip(sexo == valor, { sexo = valor }, label = { Text(valor.lowercase()) }) } }
        errores["sexo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { vm.guardar(nombre, especie, raza, anio, peso, sexo, onGuardado) }, modifier = Modifier.fillMaxWidth()) { Text("Guardar mascota") }
    }
}
