package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.ui.components.HistorialItem
import com.tecsup.huellitasvet.ui.components.GraficoPeso
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel
import java.time.LocalDate

@Composable
fun FichaMascotaScreen(vm: MascotaViewModel, editable: Boolean, onEditar: () -> Unit, onPedir: () -> Unit, onEliminado: () -> Unit, onNombreMascota: (String) -> Unit = {}) {
    val mascota by vm.mascota.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val pesos by vm.pesos.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var pestana by remember { mutableIntStateOf(1) }
    var confirmar by remember { mutableStateOf(false) }
    val actual = mascota
    if (actual == null) { Text("Cargando mascota…", Modifier.padding(20.dp)); return }
    androidx.compose.runtime.LaunchedEffect(actual.nombre) { onNombreMascota(actual.nombre) }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MascotaAvatar(actual.especie, tamano = 66.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(actual.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${actual.raza} · ${LocalDate.now().year - actual.anioNacimiento} años", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${actual.especie.lowercase().replaceFirstChar { it.uppercase() }} · ${actual.sexo.lowercase()} · ${actual.peso} kg", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (editable) Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onPedir, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Pedir cita", fontSize = 14.sp) }
            OutlinedButton(onClick = onEditar, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Editar", fontSize = 14.sp) }
        }
        if (mensaje != null) Text(mensaje.orEmpty(), Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error)
        TabRow(selectedTabIndex = pestana, containerColor = MaterialTheme.colorScheme.background) {
            Tab(pestana == 0, onClick = { pestana = 0 }, text = { Text("Datos") })
            Tab(pestana == 1, onClick = { pestana = 1 }, text = { Text("Historial clínico") })
        }
        if (pestana == 0) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Información de ${actual.nombre}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("Raza: ${actual.raza}", fontSize = 14.sp)
                Text("Año de nacimiento: ${actual.anioNacimiento}", fontSize = 14.sp)
                Text("Peso actual: ${actual.peso} kg", fontSize = 14.sp)
                Text("Sexo: ${actual.sexo.lowercase().replaceFirstChar { it.uppercase() }}", fontSize = 14.sp)
                GraficoPeso(pesos)
                if (editable) TextButton(onClick = { confirmar = true }) { Text("Eliminar mascota", color = MaterialTheme.colorScheme.error) }
            }
        } else LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("Historial clínico", Modifier.padding(top = 10.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
            if (historial.isEmpty()) item { Text("Aún no tiene atenciones", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) }
            itemsIndexed(historial, key = { _, item -> item.cita.id }) { indice, item ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(Modifier.width(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.padding(top = 18.dp).size(10.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50)))
                        if (indice < historial.lastIndex) Box(Modifier.width(2.dp).height(96.dp).background(MaterialTheme.colorScheme.primaryContainer))
                    }
                    HistorialItem(item, Modifier.weight(1f))
                }
            }
        }
    }
    if (confirmar) AlertDialog(
        onDismissRequest = { confirmar = false }, title = { Text("Eliminar mascota") },
        text = { Text("¿Quieres eliminar a ${actual.nombre}?") },
        confirmButton = { TextButton(onClick = { confirmar = false; vm.eliminar(onEliminado) }) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Volver") } }
    )
}
