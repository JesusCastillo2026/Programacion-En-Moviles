package com.tecsup.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lab09.data.local.toPostModel
import com.tecsup.lab09.ui.components.PostCard
import com.tecsup.lab09.viewmodel.PostViewModel

@Composable
fun FavoritesScreen(viewModel: PostViewModel, onOpenPost: (Int) -> Unit) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text("Mis favoritos", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
        if (favorites.isEmpty()) {
            Text("Todavía no guardaste publicaciones. Abre un post y presiona Guardar en favoritos.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites, key = { it.id }) { favorite ->
                    PostCard(post = favorite.toPostModel()) { onOpenPost(favorite.id) }
                }
            }
        }
    }
}
