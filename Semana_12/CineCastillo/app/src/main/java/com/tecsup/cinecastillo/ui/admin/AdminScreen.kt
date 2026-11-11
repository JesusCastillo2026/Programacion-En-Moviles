package com.tecsup.cinecastillo.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.cinecastillo.ui.lista.MovieListUiState

@Composable
fun AdminScreen(state: MovieListUiState, onSync: () -> Unit, onClear: () -> Unit) {
    var confirmClear by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Panel de administración", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text("${state.movies.size}", style = MaterialTheme.typography.headlineLarge)
                    Text("Películas guardadas")
                }
                Column {
                    Text("${state.movies.count { it.favorite }}", style = MaterialTheme.typography.headlineLarge)
                    Text("Favoritas")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onSync, modifier = Modifier.fillMaxWidth()) { Text("Sincronizar catálogo") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { confirmClear = true }, modifier = Modifier.fillMaxWidth()) { Text("Vaciar caché local") }
        Spacer(Modifier.height(12.dp))
        Text("La caché y sus cifras corresponden a este dispositivo.", style = MaterialTheme.typography.bodySmall)
    }
    if (confirmClear) AlertDialog(
        onDismissRequest = { confirmClear = false },
        title = { Text("¿Vaciar caché?") },
        text = { Text("Se eliminarán las películas y favoritos guardados en este dispositivo.") },
        confirmButton = { TextButton(onClick = { confirmClear = false; onClear() }) { Text("Vaciar") } },
        dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancelar") } }
    )
}
