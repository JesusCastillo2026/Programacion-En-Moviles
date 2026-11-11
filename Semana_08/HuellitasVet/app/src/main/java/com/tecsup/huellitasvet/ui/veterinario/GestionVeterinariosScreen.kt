package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.RequisitosContrasenaCard
import com.tecsup.huellitasvet.viewmodel.VeterinariosViewModel

@Composable
fun GestionVeterinariosScreen(vm: VeterinariosViewModel) {
    val veterinarios by vm.veterinarios.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var mostrarRequisitos by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Equipo veterinario", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Crea y administra las cuentas del personal veterinario.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (!mostrarFormulario) {
            Button(onClick = { mostrarFormulario = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Text("Registrar veterinario")
            }
        } else {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("Datos de la cuenta", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(correo, { correo = it }, label = { Text("Correo electrónico") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(telefono, { telefono = it.filter(Char::isDigit).take(9) }, label = { Text("Teléfono (9 dígitos)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(distrito, { distrito = it }, label = { Text("Distrito") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        clave, { clave = it }, label = { Text("Contraseña") }, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) mostrarRequisitos = true }
                    )
                    if (mostrarRequisitos) RequisitosContrasenaCard(clave)
                    OutlinedTextField(
                        confirmarClave, { confirmarClave = it }, label = { Text("Confirmar contraseña") }, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
                    )
                    Text("La cuenta se crea con el rol Veterinario y no tendrá acceso a la administración del equipo.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { mostrarFormulario = false }, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                        Button(
                            onClick = {
                                vm.registrar(nombre, correo, telefono, distrito, clave, confirmarClave) {
                                    nombre = ""; correo = ""; telefono = ""; distrito = ""; clave = ""; confirmarClave = ""
                                    mostrarFormulario = false
                                }
                            }, modifier = Modifier.weight(1f)
                        ) { Text("Crear cuenta") }
                    }
                }
            }
        }

        if (mensaje != null) Text(
            mensaje.orEmpty(),
            color = if (mensaje.orEmpty().startsWith("Cuenta veterinaria creada")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium
        )
        Text("Veterinarios registrados · ${veterinarios.size}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        if (veterinarios.isEmpty()) {
            Text("Aún no se han creado cuentas veterinarias.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            veterinarios.forEach { veterinario ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(veterinario.nombre, fontWeight = FontWeight.SemiBold)
                        Text(veterinario.correo, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${veterinario.telefono} · ${veterinario.distrito}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
