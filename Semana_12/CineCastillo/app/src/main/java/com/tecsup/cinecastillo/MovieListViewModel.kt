package com.tecsup.cinecastillo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cinecastillo.data.Movie
import com.tecsup.cinecastillo.data.MovieRepository
import com.tecsup.cinecastillo.notification.Notificador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Orden aplicado sobre las películas disponibles localmente. */
enum class MovieSort { POPULAR, RATING, NEWEST }

data class MovieListUiState(
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = true,
    val offline: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val favoritesOnly: Boolean = false,
    val syncCount: Int? = null,
    val sort: MovieSort = MovieSort.POPULAR
) {
    val visible: List<Movie> get() {
        val filtered = movies.filter { movie ->
            (!favoritesOnly || movie.favorite) &&
                (query.isBlank() || movie.title.contains(query.trim(), ignoreCase = true))
        }
        return when (sort) {
            MovieSort.POPULAR -> filtered
            MovieSort.RATING -> filtered.sortedByDescending { it.rating }
            MovieSort.NEWEST -> filtered.sortedByDescending { it.releaseDate }
        }
    }
}

class MovieListViewModel(private val repository: MovieRepository, private val notificador: Notificador) : ViewModel() {
    private val mutable = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = mutable.asStateFlow()

    init {
        viewModelScope.launch {
            repository.movies.collect { movies -> mutable.update { it.copy(movies = movies) } }
        }
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            mutable.update { it.copy(loading = true, error = null, syncCount = null) }
            try {
                val count = repository.sync()
                mutable.update { it.copy(loading = false, offline = false, syncCount = count) }
                notificador.mostrar("Datos actualizados", "Se guardaron $count elementos")
            } catch (e: Exception) {
                mutable.update {
                    it.copy(
                        loading = false,
                        offline = it.movies.isNotEmpty(),
                        error = if (it.movies.isEmpty()) "No se pudo cargar el catálogo. Revisa la conexión y vuelve a intentar." else null
                    )
                }
            }
        }
    }

    fun search(query: String) = mutable.update { it.copy(query = query) }
    fun sort(mode: MovieSort) = mutable.update { it.copy(sort = mode) }
    fun showFavorites(only: Boolean) = mutable.update { it.copy(favoritesOnly = only) }

    fun setFavorite(id: Int, favorite: Boolean) {
        viewModelScope.launch { repository.setFavorite(id, favorite) }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            mutable.update { it.copy(offline = false, syncCount = null) }
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MovieListViewModel(container.movies, container.notificador) as T
    }
}
