package com.saludplus.paciente.ui.screens.doctores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.EmptyState
import com.saludplus.paciente.ui.components.FotoMedico
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.components.SearchField
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario

/** Directorio de médicos con retratos ilustrativos y filtros por especialidad. */
@Composable
fun DoctoresScreen(onBack: () -> Unit, onBook: () -> Unit) {
    var categoria by rememberSaveable { mutableStateOf("") }
    var busqueda by rememberSaveable { mutableStateOf("") }
    val medicos = Repositorio.medicos.filter {
        (categoria.isEmpty() || it.especialidadId == categoria) &&
            (it.nombre.contains(busqueda.trim(), ignoreCase = true) ||
                it.especialidadNombre.contains(busqueda.trim(), ignoreCase = true))
    }
    Column(Modifier.fillMaxSize()) {
        AppBackTopBar("Doctores", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                PageHeading("Conoce al equipo", "Explora médicos por especialidad y elige una sede al reservar.")
                SearchField(busqueda, { busqueda = it }, "Buscar doctor", Modifier.padding(top = 14.dp))
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(selected = categoria.isEmpty(), onClick = { categoria = "" }, label = { Text("Todos") }) }
                    items(Repositorio.especialidades, key = { it.id }) { especialidad ->
                        FilterChip(selected = categoria == especialidad.id,
                            onClick = { categoria = especialidad.id }, label = { Text(especialidad.nombre) })
                    }
                }
            }
            items(medicos, key = { it.id }) { medico ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            FotoMedico(medico, 72.dp)
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(medico.nombre, fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium)
                                Text(medico.especialidadNombre, color = TextoSecundario)
                                Text("★ ${medico.calificacion} · ${medico.experiencia} años de experiencia",
                                    color = AzulClinico, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        val sedes = Repositorio.sedes.filter { it.id in medico.sedesIds }
                            .joinToString(" · ") { it.nombre }
                        Text("Atiende en: $sedes", style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario)
                        SaludPlusButton("Elegir sede para reservar", onClick = onBook)
                    }
                }
            }
            if (medicos.isEmpty()) {
                item { EmptyState("Sin doctores", "Prueba otra especialidad o borra la búsqueda.",
                    actionLabel = "Ver todos", onAction = { categoria = ""; busqueda = "" }) }
            }
        }
    }
}
