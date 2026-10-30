package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.saludplus.paciente.data.model.Especialidad
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.PlaceholderAvatar
import com.saludplus.paciente.ui.components.SearchField
import com.saludplus.paciente.ui.components.ReservaProgress
import com.saludplus.paciente.ui.components.EmptyState
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.CelesteSuave
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
/** Muestra el catálogo filtrable de especialidades que inicia la reserva. */
fun EspecialidadesScreen(sedeId: String, onBack: () -> Unit, onSelect: (String) -> Unit, seleccionadoId: String = "") {
    var consulta by rememberSaveable { mutableStateOf("") }
    val disponibles = Repositorio.especialidadesPorSede(sedeId).map { it.id }.toSet()
    val resultados = Repositorio.buscarEspecialidades(consulta).filter { it.id in disponibles }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Especialidades", onBack)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                modifier = Modifier.fillMaxSize().imePadding()) {
                item {
                    ReservaProgress(2)
                    PageHeading("Encuentra atención", "Especialidades en ${Repositorio.obtenerSede(sedeId)?.nombre ?: "la sede elegida"}")
                    SearchField(consulta, { consulta = it }, "Buscar especialidad", Modifier.padding(top = 14.dp, bottom = 12.dp))
                }
                items(resultados, key = { it.id }) { especialidad ->
                    EspecialidadItem(especialidad, onClick = { onSelect(especialidad.id) }, seleccionado = especialidad.id == seleccionadoId)
                }
                if (resultados.isEmpty()) {
                    item { EmptyState("Sin coincidencias", "Prueba otro nombre o vuelve a ver todas las especialidades.",
                        actionLabel = "Limpiar búsqueda", onAction = { consulta = "" }) }
                }
            }
    }
}

@Composable
private fun EspecialidadItem(especialidad: Especialidad, onClick: () -> Unit, seleccionado: Boolean) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (seleccionado) CelesteSuave else Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlaceholderAvatar(especialidad.simbolo, 48.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(especialidad.nombre, fontWeight = FontWeight.Bold)
                if (seleccionado) Text("Seleccionada", color = AzulClinico, style = MaterialTheme.typography.labelSmall)
                Text(especialidad.descripcion, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver médicos", tint = AzulClinico)
        }
    }
}

