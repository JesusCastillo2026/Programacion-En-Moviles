package com.tecsup.cinecastillo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tecsup.cinecastillo.MovieListUiState
import com.tecsup.cinecastillo.data.Movie

@Composable
fun CatalogScreen(
    state: MovieListUiState,
    onSearch: (String) -> Unit,
    onFavorites: (Boolean) -> Unit,
    onSync: () -> Unit,
    onDetail: (Int) -> Unit,
    onFavorite: (Int, Boolean) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Películas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = state.query, onValueChange = onSearch, label = { Text("Buscar en el catálogo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { onFavorites(false) }) { Text("Todas") }
            TextButton(onClick = { onFavorites(true) }) { Text("Favoritas") }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onSync) { Text("Actualizar") }
        }
        if (state.offline) Text("Sin conexión · mostrando películas guardadas", color = MaterialTheme.colorScheme.primary)
        if (state.loading && state.movies.isEmpty()) CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        if (state.error != null) {
            Text(state.error, color = MaterialTheme.colorScheme.error)
            Button(onClick = onSync) { Text("Reintentar") }
        }
        if (!state.loading && state.error == null && state.visible.isEmpty()) {
            Text(if (state.movies.isEmpty()) "No hay películas guardadas." else "No se encontraron resultados.")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.visible, key = { it.id }) { movie ->
                MovieCard(movie, onDetail = { onDetail(movie.id) }, onFavorite = { onFavorite(movie.id, !movie.favorite) })
            }
        }
    }
}

@Composable
private fun MovieCard(movie: Movie, onDetail: () -> Unit, onFavorite: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onDetail)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = movie.posterUrl, contentDescription = "Póster de ${movie.title}", modifier = Modifier.size(width = 72.dp, height = 105.dp), contentScale = ContentScale.Crop)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium)
                Text(movie.releaseDate.take(4), style = MaterialTheme.typography.bodySmall)
                Text("★ %.1f".format(movie.rating), style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onFavorite) {
                Icon(if (movie.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = if (movie.favorite) "Quitar de favoritos" else "Guardar en favoritos")
            }
        }
    }
}
