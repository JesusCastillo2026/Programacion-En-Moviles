package com.tecsup.cinecastillo.ui.components
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.tecsup.cinecastillo.ui.UiState
@Composable fun ErrorView(mensaje: String, reintentar: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(mensaje, color = MaterialTheme.colorScheme.error)
            Button(onClick = reintentar) { Text("Reintentar") }
        }
    }
}
@Composable fun <T> EstadoContenido(estado: UiState<T>, reintentar: () -> Unit, contenido: @Composable (T) -> Unit) {
    when (estado) {
        UiState.Cargando -> Cargando()
        is UiState.Error -> ErrorView(estado.mensaje, reintentar)
        is UiState.Exito -> contenido(estado.datos)
    }
}
