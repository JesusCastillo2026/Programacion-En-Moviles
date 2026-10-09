package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
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
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.CelesteSuave
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
fun EspecialidadesScreen(onBack: () -> Unit, onSelect: (String) -> Unit) {
    var consulta by remember { mutableStateOf("") }
    val resultados = Repositorio.buscarEspecialidades(consulta)

    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Especialidades", onBack)
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            PageHeading("Encuentra atención", "Elige el área de salud que buscas")
            SearchField(consulta, { consulta = it }, "Buscar especialidad", Modifier.padding(top = 14.dp, bottom = 12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(resultados, key = { it.id }) { especialidad ->
                    EspecialidadItem(especialidad, onClick = { onSelect(especialidad.id) })
                }
                if (resultados.isEmpty()) {
                    item { Text("No encontramos especialidades con ese nombre.", color = TextoSecundario, modifier = Modifier.padding(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EspecialidadItem(especialidad: Especialidad, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlaceholderAvatar(especialidad.simbolo, 48.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(especialidad.nombre, fontWeight = FontWeight.Bold)
                Text(especialidad.descripcion, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver médicos", tint = AzulClinico)
        }
    }
}

