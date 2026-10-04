package com.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TarjetaCita
import com.saludplus.citas.ui.components.TituloSeccion

@Composable
fun MisCitasScreen(onDetalle: (Int) -> Unit) {
    val usuario = Repositorio.usuarioActual
    val citas = usuario?.let { Repositorio.citasDelUsuario(it.id) }.orEmpty()
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSeccion("Tus citas")
        Text("Revisa tus próximas atenciones.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (citas.isEmpty()) item {
                EstadoVacio("Aún no tienes citas", "Explora las especialidades para agendar.")
            }
            items(citas, key = { it.id }) { cita ->
                TarjetaCita(cita) { onDetalle(cita.id) }
            }
        }
    }
}
