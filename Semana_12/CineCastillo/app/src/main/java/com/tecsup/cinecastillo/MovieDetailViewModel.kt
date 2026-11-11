package com.tecsup.cinecastillo

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

    init {
        viewModelScope.launch { repository.movie(id).collect { movie -> mutable.value = mutable.value.copy(movie = movie) } }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            mutable.value = mutable.value.copy(loading = true, error = null)
            try {
                repository.refreshDetail(id)
                mutable.value = mutable.value.copy(loading = false, offline = false)
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
        viewModelScope.launch { repository.setFavorite(id, favorite) }
    }

    class Factory(private val id: Int, private val repository: MovieRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MovieDetailViewModel(id, repository) as T
    }
}

