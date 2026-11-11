package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel

@Composable
fun FormularioMascotaScreen(vm: MascotaViewModel, onGuardado: () -> Unit, onCancelar: () -> Unit = onGuardado) {
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
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box {
                MascotaAvatar(especie, tamano = 76.dp)
                Surface(Modifier.align(Alignment.BottomEnd), shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Default.Add, contentDescription = "Elegir imagen de mascota", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(4.dp))
                }
            }
        }
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, isError = errores.containsKey("nombre"), supportingText = { errores["nombre"]?.let { Text(it) } }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
        Text("Especie", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("PERRO" to "Perro", "GATO" to "Gato", "OTRO" to "Otro").forEach { (valor, titulo) -> FilterChip(especie == valor, { especie = valor }, label = { Text(titulo) }) } }
        errores["especie"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(raza, { raza = it }, label = { Text("Raza") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(anio, { anio = it }, label = { Text("Año de nacimiento") }, isError = errores.containsKey("anio"), supportingText = { errores["anio"]?.let { Text(it) } }, shape = RoundedCornerShape(14.dp), modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(peso, { peso = it }, label = { Text("Peso (kg)") }, isError = errores.containsKey("peso"), supportingText = { errores["peso"]?.let { Text(it) } }, shape = RoundedCornerShape(14.dp), modifier = Modifier.weight(1f), singleLine = true)
        }
        Text("Sexo", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("MACHO" to "Macho", "HEMBRA" to "Hembra").forEach { (valor, titulo) -> FilterChip(sexo == valor, { sexo = valor }, label = { Text(titulo) }) } }
        errores["sexo"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { vm.guardar(nombre, especie, raza, anio, peso, sexo, onGuardado) }, shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) { Text("Guardar mascota", fontSize = 14.sp) }
        OutlinedButton(onClick = onCancelar, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) { Text("Cancelar", fontSize = 14.sp) }
    }
}
