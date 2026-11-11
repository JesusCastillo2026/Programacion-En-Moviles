package com.tecsup.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lab09.ui.components.ErrorState
import com.tecsup.lab09.ui.components.LoadingState
import com.tecsup.lab09.viewmodel.PostViewModel

@Composable
fun PostDetailScreen(postId: Int, viewModel: PostViewModel) {
    val state by viewModel.detail.collectAsStateWithLifecycle()
    LaunchedEffect(postId) { viewModel.loadPost(postId) }

    when {
        state.isLoading -> LoadingState("Cargando publicación…")
        state.error != null -> ErrorState(state.error.orEmpty()) { viewModel.loadPost(postId) }
        state.post != null -> {
            val post = state.post!!
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Detalle de publicación", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(value = post.id.toString(), onValueChange = {}, label = { Text("ID") }, readOnly = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = post.userId.toString(), onValueChange = {}, label = { Text("User ID") }, readOnly = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = post.title, onValueChange = {}, label = { Text("Título") }, readOnly = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = post.body, onValueChange = {}, label = { Text("Contenido") }, readOnly = true, minLines = 4, modifier = Modifier.fillMaxWidth())
                Button(onClick = { viewModel.toggleFavorite(post) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(if (state.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null)
                    Text(if (state.isFavorite) "  Quitar de favoritos" else "  Guardar en favoritos")
                }
            }
        }
    }
}
