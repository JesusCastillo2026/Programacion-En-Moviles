package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TarjetaEspecialidad
import com.saludplus.citas.ui.components.TituloSeccion

/** Filtra especialidades en tiempo real mientras cambia el texto de búsqueda. */
@Composable
fun EspecialidadesScreen(onElegir: (Int) -> Unit) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    val lista = Repositorio.buscarEspecialidades(busqueda)
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSeccion("Encuentra atención para ti")
        OutlinedTextField(busqueda, { busqueda = it }, modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar especialidad") }, singleLine = true)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (lista.isEmpty()) item { EstadoVacio("Sin resultados", "Prueba otra búsqueda.") }
            items(lista, key = { it.id }) { especialidad ->
                TarjetaEspecialidad(especialidad) { onElegir(especialidad.id) }
            }
        }
    }
}
