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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lab09.ui.components.ErrorState
import com.tecsup.lab09.ui.components.LoadingState
import com.tecsup.lab09.ui.components.PostCard
import com.tecsup.lab09.viewmodel.PostViewModel

@Composable
fun HomeScreen(viewModel: PostViewModel, onOpenPost: (Int) -> Unit) {
    val state by viewModel.posts.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val visiblePosts = remember(state.posts, query) {
        state.posts.filter { post ->
            post.title.contains(query, ignoreCase = true) || post.body.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 12.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("LECTURA DIGITAL · TECSUP", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .82f))
                Text("Explora publicaciones", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                Text("Consulta, descubre y guarda lo que quieras volver a leer.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Publicaciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            AssistChip(onClick = viewModel::loadPosts, label = { Text("Actualizar") }, leadingIcon = { Icon(Icons.Outlined.Refresh, null) })
        }
        Text("${state.posts.size} artículos de JSONPlaceholder", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Buscar por título o contenido") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) }
        )

        when {
            state.isLoading -> LoadingState("Cargando publicaciones…")
            state.error != null -> ErrorState(state.error.orEmpty(), viewModel::loadPosts)
            visiblePosts.isEmpty() -> Text("No encontramos publicaciones con ese texto.", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visiblePosts, key = { it.id }) { post -> PostCard(post = post) { onOpenPost(post.id) } }
            }
        }
    }
}
