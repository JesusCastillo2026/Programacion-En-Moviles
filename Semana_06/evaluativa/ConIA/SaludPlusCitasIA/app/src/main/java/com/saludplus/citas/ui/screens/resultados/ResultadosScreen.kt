package com.saludplus.citas.ui.screens.resultados

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.ui.components.TituloSeccion

data class ResultadoEjemplo(val nombre: String, val fecha: String, val estado: String)

@Composable
fun ResultadosScreen() {
    val resultados = listOf(
        ResultadoEjemplo("Hemograma completo", "Ejemplo de laboratorio", "Disponible"),
        ResultadoEjemplo("Perfil lipídico", "Ejemplo de laboratorio", "En proceso")
    )
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSeccion("Resultados")
        Text("Datos de demostración, no corresponden a un paciente real.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(resultados) { resultado ->
                Card {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(resultado.nombre)
                        Text(resultado.fecha)
                        Text(resultado.estado, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
