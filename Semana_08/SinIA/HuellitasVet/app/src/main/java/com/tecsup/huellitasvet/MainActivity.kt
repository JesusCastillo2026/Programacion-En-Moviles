package com.tecsup.huellitasvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.ui.theme.VetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VetTheme {
                val servicios = listOf(
                    Servicio(1, "Consulta general", "Revisión de salud", 30, 60.0, true, "heart"),
                    Servicio(2, "Vacunación", "Protección preventiva", 30, 45.0, true, "syringe"),
                    Servicio(3, "Baño y corte", "Cuidado y limpieza", 60, 50.0, true, "scissors")
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Text("HuellitasVet", style = MaterialTheme.typography.headlineMedium) }
                    items(servicios) { servicio ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(18.dp)) {
                                Text(servicio.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(servicio.descripcion)
                                Text("${servicio.duracionMin} min · S/ ${servicio.precio}")
                            }
                        }
                    }
                }
            }
        }
    }
}
