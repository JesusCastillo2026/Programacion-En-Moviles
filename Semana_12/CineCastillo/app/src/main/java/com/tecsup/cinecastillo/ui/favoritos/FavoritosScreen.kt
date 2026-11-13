package com.tecsup.cinecastillo.ui.favoritos
import androidx.compose.runtime.Composable
import com.tecsup.cinecastillo.ui.lista.*
@Composable fun FavoritosScreen(state: MovieListUiState, abrir: (Int)->Unit, alternar: (Int)->Unit) {
    CatalogScreen(state, {}, {}, {}, {}, abrir, { id, _ -> alternar(id) }, soloFavoritos=true)
}
