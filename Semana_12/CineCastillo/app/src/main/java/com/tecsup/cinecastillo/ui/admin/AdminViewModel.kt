package com.tecsup.cinecastillo.ui.admin
import androidx.lifecycle.*
import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.data.repository.MovieRepository
import com.tecsup.cinecastillo.notification.Notificador
import com.tecsup.cinecastillo.ui.UiState
import com.tecsup.cinecastillo.ui.lista.MovieListUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class AdminViewModel(private val repo: MovieRepository, private val notificador: Notificador): ViewModel() {
    private val mutable = MutableStateFlow<UiState<MovieListUiState>>(UiState.Cargando)
    val estado = mutable.asStateFlow()
    init { viewModelScope.launch { repo.movies.collect {
        mutable.value = UiState.Exito(MovieListUiState(movies=it, loading=false))
    } } }
    fun sincronizar() { viewModelScope.launch {
        repo.refrescar().onSuccess { notificador.mostrar("Datos actualizados", "Se guardaron $it elementos") }
            .onFailure { mutable.value = UiState.Error("No se pudo sincronizar. Revisa la conexión.") }
    } }
    fun limpiar() { viewModelScope.launch { repo.limpiarCache() } }
    class Factory(private val container: AppContainer): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(modelClass: Class<T>): T = AdminViewModel(container.movies, container.notificador) as T
    }
}
