package com.saludplus.paciente.ui.screens.resultados

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario

private data class ResultadoEjemplo(val nombre: String, val fecha: String, val estado: String)

@Composable
/** Pantalla de reto con muestras locales de resultados clínicos de demostración. */
fun ResultadosScreen() {
    val resultados = listOf(
        ResultadoEjemplo("Hemograma completo", "18 oct 2026", "Disponible"),
        ResultadoEjemplo("Perfil lipídico", "12 oct 2026", "En revisión"),
        ResultadoEjemplo("Control preventivo", "04 oct 2026", "Disponible")
    )
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        PageHeading("Resultados", "Documentos de ejemplo de tu atención")
        LazyColumn(modifier = Modifier.padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(resultados) { item ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AzulClinico)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.nombre, fontWeight = FontWeight.Bold)
                            Text(item.fecha, color = TextoSecundario)
                        }
                        Text(item.estado, color = AzulClinico, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

