package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.ReservaProgress
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.CelesteSuave
import com.saludplus.paciente.ui.theme.TextoSecundario

/** Primera decisión obligatoria del agendamiento: local de atención presencial. */
@Composable
fun SedesScreen(onBack: () -> Unit, onSelect: (String) -> Unit, seleccionadoId: String = "") {
    Column(Modifier.fillMaxSize()) {
        AppBackTopBar("Sedes", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                ReservaProgress(1)
                PageHeading("¿Dónde te atendemos?", "Selecciona una sede antes de elegir tu especialidad y horario.")
            }
            items(Repositorio.sedes, key = { it.id }) { sede ->
                val medicos = Repositorio.medicos.count { sede.id in it.sedesIds }
                Card(onClick = { onSelect(sede.id) }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if (sede.id == seleccionadoId) CelesteSuave else Color.White)) {
                    Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = AzulClinico)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(sede.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${sede.distrito} · $medicos profesionales", color = TextoSecundario,
                                style = MaterialTheme.typography.bodySmall)
                            Text(sede.referencia, color = AzulClinico, style = MaterialTheme.typography.labelMedium)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Elegir ${sede.nombre}", tint = AzulClinico)
                    }
                }
            }
        }
    }
}
