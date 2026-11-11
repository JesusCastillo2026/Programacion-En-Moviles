package com.castillo.campusnav.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.castillo.campusnav.data.*
import com.castillo.campusnav.ui.*

/** Conserva búsqueda y filtro al volver del detalle o recrear la actividad. */
@Composable
fun DirectoryScreen(store: CampusStore, onBack: () -> Unit, onDetail: (Int) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var filterName by rememberSaveable { mutableStateOf(DirectoryFilter.ALL.name) }
    val filter = DirectoryFilter.valueOf(filterName)
    val results = filterStudents(store.directory(), query, filter, store.favorites)
    Page("Directorio de alumnos", onBack) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("Encuentra tu comunidad", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(6.dp))
                Text("8 perfiles de demostración para explorar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(18.dp), label = { Text("Buscar por nombre o carrera") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Default.Close, "Limpiar búsqueda") } })
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DirectoryFilter.entries.forEach { option ->
                        FilterChip(selected = filter == option, onClick = { filterName = option.name }, label = { Text(option.label) })
                    }
                }
            }
            item { SectionLabel("${results.size} RESULTADOS · DATOS DE EJEMPLO") }
            if (results.isEmpty()) item {
                WhiteCard {
                    Icon(Icons.Default.SearchOff, null, tint = Violet, modifier = Modifier.size(38.dp))
                    Text("No encontramos perfiles", style = MaterialTheme.typography.titleMedium)
                    Text(if (filter == DirectoryFilter.FAVORITES) "Guarda un perfil con la estrella o prueba otra búsqueda." else "Prueba otro nombre o elimina los filtros.")
                    TextButton(onClick = { query = ""; filterName = DirectoryFilter.ALL.name }) { Text("Ver todos") }
                }
            }
            items(results, key = { it.id }) { student ->
                Card(onClick = { onDetail(student.id) }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
                    Row(Modifier.fillMaxWidth().padding(start = 16.dp, top = 18.dp, bottom = 18.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Avatar(student.name, student.id == 1)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(student.name, style = MaterialTheme.typography.titleMedium)
                            Text(student.career, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Ver expediente →", style = MaterialTheme.typography.labelSmall, color = Violet)
                        }
                        IconToggleButton(checked = student.id in store.favorites, onCheckedChange = { store.toggleFavorite(student.id) }) {
                            Icon(if (student.id in store.favorites) Icons.Default.Star else Icons.Default.StarBorder,
                                if (student.id in store.favorites) "Quitar a ${student.name} de favoritos" else "Guardar a ${student.name} en favoritos", tint = Violet)
                        }
                    }
                }
            }
        }
    }
}
