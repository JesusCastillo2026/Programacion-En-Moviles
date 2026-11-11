package com.tecsup.cinecastillo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder

/** Créditos visibles para identificar la fuente externa de datos e imágenes. */
@Composable
fun CreditsScreen() {
    val context = LocalContext.current
    val svgLoader = ImageLoader.Builder(context).components { add(SvgDecoder.Factory()) }.build()
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Acerca de Cine Castillo", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        Text("Proyecto académico · Jesús Castillo Sumire · TECSUP")
        Spacer(Modifier.height(28.dp))
        AsyncImage(
            model = "file:///android_asset/tmdb.svg",
            imageLoader = svgLoader,
            contentDescription = "Logotipo oficial de TMDB",
            modifier = Modifier.size(92.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Películas e imágenes: The Movie Database (TMDB).", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "This product uses the TMDB API but is not endorsed or certified by TMDB.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
