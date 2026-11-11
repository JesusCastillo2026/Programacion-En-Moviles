package com.tecsup.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
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
import com.tecsup.lab09.ui.components.PostCard
import com.tecsup.lab09.viewmodel.PostViewModel

@Composable
fun HomeScreen(viewModel: PostViewModel, onOpenPost: (Int) -> Unit) {
    val state by viewModel.posts.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text("Publicaciones", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text("Contenido obtenido desde JSONPlaceholder", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = viewModel::loadPosts, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
            Icon(Icons.Outlined.Refresh, contentDescription = null)
            Text("  Actualizar")
        }

        when {
            state.isLoading -> LoadingState("Cargando publicaciones…")
            state.error != null -> ErrorState(state.error.orEmpty(), viewModel::loadPosts)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.posts, key = { it.id }) { post ->
                    PostCard(post = post) { onOpenPost(post.id) }
                }
            }
        }
    }
}
