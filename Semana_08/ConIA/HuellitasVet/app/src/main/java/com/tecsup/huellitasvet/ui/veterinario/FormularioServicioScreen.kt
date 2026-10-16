package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel

@Composable
fun FormularioServicioScreen(
    vm: ServiciosViewModel,
    id: Int,
    onGuardado: () -> Unit,
    onCancelar: () -> Unit = onGuardado
) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf(30) }
    var precio by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("heart") }
    var activo by remember { mutableStateOf(true) }
    var menuCategoriasAbierto by remember { mutableStateOf(false) }
    val categorias = remember { mutableStateListOf<String>() }
    val opcionesCategoria = listOf("Consulta general", "Vacunación", "Baño", "Corte de pelo", "Desparasitación", "Emergencia")
    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = .48f),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = .78f),
        cursorColor = MaterialTheme.colorScheme.primary
    )
    val coloresChip = FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .52f),
        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    )
    LaunchedEffect(id, servicios) {
        servicios.find { it.id == id }?.let {
            nombre = it.nombre
            descripcion = it.descripcion
            duracion = it.duracionMin
            precio = it.precio.toString()
            icono = it.icono
            activo = it.activo
            categorias.clear()
            categorias.addAll(it.categorias.split("|").map { categoria -> categoria.trim() }.filter { categoria -> categoria.isNotEmpty() })
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del servicio") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp),
            singleLine = true,
            colors = coloresCampo
        )
        Text("Tipo de servicio", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = categorias.joinToString(" · "),
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona uno o varios tipos") },
                placeholder = { Text("Consulta, vacunas, corte...") },
                trailingIcon = { Text(if (menuCategoriasAbierto) "▴" else "▾", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth().clickable { menuCategoriasAbierto = true },
                shape = RoundedCornerShape(13.dp),
                colors = coloresCampo
            )
            DropdownMenu(
                expanded = menuCategoriasAbierto,
                onDismissRequest = { menuCategoriasAbierto = false },
                modifier = Modifier.fillMaxWidth(.9f),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                opcionesCategoria.forEach { opcion ->
                    val marcada = opcion in categorias
                    DropdownMenuItem(
                        text = { Text(opcion, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                        leadingIcon = {
                            Checkbox(
                                checked = marcada,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.primary.copy(alpha = .55f)
                                )
                            )
                        },
                        onClick = {
                            if (marcada) categorias.remove(opcion) else categorias.add(opcion)
                        }
                    )
                }
            }
        }
        if (categorias.isNotEmpty()) {
            Text("Seleccionado: ${categorias.joinToString(" · ")}", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        }
        Text("Duración", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(15, 30, 45, 60).forEach { valor ->
                FilterChip(
                    selected = duracion == valor,
                    onClick = { duracion = valor },
                    label = { Text("$valor min") },
                    modifier = Modifier.weight(1f),
                    colors = coloresChip
                )
            }
        }
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it.filter { caracter -> caracter.isDigit() || caracter == '.' || caracter == ',' } },
            label = { Text("Precio (S/)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp),
            singleLine = true,
            colors = coloresCampo
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp),
            minLines = 2,
            colors = coloresCampo
        )
        Text("Ícono", style = MaterialTheme.typography.titleSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("heart", "syringe", "scissors").forEach { valor ->
                val simbolo = when (valor) {
                    "syringe" -> Icons.Default.Vaccines
                    "scissors" -> Icons.Default.ContentCut
                    else -> Icons.Default.Favorite
                }
                FilterChip(
                    selected = icono == valor,
                    onClick = { icono = valor },
                    label = { Icon(simbolo, contentDescription = valor) },
                    modifier = Modifier.weight(1f),
                    colors = coloresChip
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Servicio activo", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                Text("Los clientes podrán pedirlo", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = .82f))
            }
            Switch(
                checked = activo,
                onCheckedChange = { activo = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = .35f)
                )
            )
        }
        if (mensaje != null) Text(mensaje.orEmpty(), color = MaterialTheme.colorScheme.error)
        Button(
            onClick = {
                vm.guardar(
                    Servicio(id, nombre.trim(), descripcion.trim(), duracion, precio.replace(',', '.').toDoubleOrNull() ?: 0.0, activo, icono, categorias.joinToString("|")),
                    onGuardado
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp)
        ) { Text("Guardar servicio", fontSize = 14.sp) }
        OutlinedButton(
            onClick = onCancelar, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text("Cancelar", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
