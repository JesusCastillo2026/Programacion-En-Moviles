package com.tecsup.cinecastillo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tecsup.cinecastillo.MovieDetailUiState

@Composable
fun DetailScreen(state: MovieDetailUiState, onBack: () -> Unit, onRefresh: () -> Unit, onFavorite: (Boolean) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        TextButton(onClick = onBack) { Text("← Volver") }
        val movie = state.movie
        if (movie == null) {
            if (state.loading) CircularProgressIndicator()
            else {
                Text(state.error ?: "Película no disponible")
                Button(onClick = onRefresh) { Text("Reintentar") }
            }
            return@Column
        }
        if (state.offline) Text("Detalle guardado · sin conexión", color = MaterialTheme.colorScheme.primary)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = movie.posterUrl, contentDescription = "Póster de ${movie.title}", modifier = Modifier.size(width = 115.dp, height = 172.dp), contentScale = ContentScale.Crop)
            Column {
                Text(movie.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(movie.releaseDate.ifEmpty { "Fecha no disponible" })
                Text("★ %.1f / 10".format(movie.rating))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Sinopsis", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(movie.overview.ifEmpty { "Sin sinopsis disponible en español." }, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(22.dp))
        Button(onClick = { onFavorite(!movie.favorite) }, modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.Icon(if (movie.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null)
            Text(if (movie.favorite) " Quitar de favoritos" else " Guardar en favoritos")
        }
    }
}
