package com.tecsup.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lab09.ui.components.ErrorState
import com.tecsup.lab09.ui.components.LoadingState
import com.tecsup.lab09.viewmodel.PostViewModel

@Composable
fun TodosScreen(viewModel: PostViewModel) {
    val state by viewModel.todos.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text("Tareas de ejemplo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text("Ejercicio adicional: consumo del recurso /todos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = viewModel::loadTodos, modifier = Modifier.padding(vertical = 10.dp)) {
            Icon(Icons.Outlined.Refresh, contentDescription = null)
            Text("  Actualizar")
        }
        when {
            state.isLoading -> LoadingState("Cargando tareas…")
            state.error != null -> ErrorState(state.error.orEmpty(), viewModel::loadTodos)
            else -> LazyColumn(
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.todos, key = { it.id }) { todo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                            Text("${if (todo.completed) "✓" else "○"}  ", color = if (todo.completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant)
                            Column {
                                Text(todo.title.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Medium)
                                Text("Tarea ${todo.id} · Usuario ${todo.userId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
