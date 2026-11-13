package com.tecsup.cinecastillo.ui.detalle

import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.ui.UiState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cinecastillo.domain.model.Movie
import com.tecsup.cinecastillo.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MovieDetailUiState(val movie: Movie? = null, val loading: Boolean = true, val offline: Boolean = false, val error: String? = null)

class MovieDetailViewModel(private val id: Int, private val repository: MovieRepository) : ViewModel() {
    private val mutable = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = mutable.asStateFlow()
    val estado = mutable.map { if (it.loading && it.movie == null) UiState.Cargando else if (it.error != null) UiState.Error(it.error) else UiState.Exito(it) }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    init {
        viewModelScope.launch { repository.movie(id).collect { movie -> mutable.value = mutable.value.copy(movie = movie) } }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            mutable.value = mutable.value.copy(loading = true, error = null)
            try {
                repository.obtenerDetalle(id).getOrThrow()
                mutable.value = mutable.value.copy(loading = false, offline = false)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                val hasCache = mutable.value.movie != null
                mutable.value = mutable.value.copy(
                    loading = false,
                    offline = hasCache,
                    error = if (hasCache) null else "No se pudo cargar esta película."
                )
            }
        }
    }

    fun setFavorite(favorite: Boolean) {
        viewModelScope.launch { repository.alternarFavorito(id) }
    }

    class Factory(private val id: Int, private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MovieDetailViewModel(id, container.movies) as T
    }
}

