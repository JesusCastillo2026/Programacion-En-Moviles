package com.tecsup.cinecastillo.ui.favoritos
import androidx.lifecycle.*
import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.data.repository.MovieRepository
import com.tecsup.cinecastillo.ui.UiState
import com.tecsup.cinecastillo.ui.lista.MovieListUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class FavoritosViewModel(private val repo: MovieRepository) : ViewModel() {
    val estado: StateFlow<UiState<MovieListUiState>> = repo.favorites.map {
        UiState.Exito(MovieListUiState(movies=it, loading=false, favoritesOnly=true))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)
    fun alternar(id: Int) { viewModelScope.launch { repo.alternarFavorito(id) } }
    class Factory(private val container: AppContainer): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(modelClass: Class<T>): T = FavoritosViewModel(container.movies) as T
    }
}
