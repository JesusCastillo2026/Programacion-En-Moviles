package com.tecsup.cinecastillo

import com.tecsup.cinecastillo.data.Movie
import com.tecsup.cinecastillo.data.MovieRepository
import com.tecsup.cinecastillo.notification.Notificador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {
    private val film = Movie(11, "Película de prueba", "Historia", null, "2026-01-01", 8.2)

    @Test fun cargaExitosaMuestraPeliculas() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = FakeMovies(remote = listOf(film))
            val vm = MovieListViewModel(fake, FakeNotificador())
            advanceUntilIdle()
            assertEquals(1, vm.uiState.value.movies.size)
            assertFalse(vm.uiState.value.loading)
            assertEquals(1, vm.uiState.value.syncCount)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun falloSinCacheMuestraReintento() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = MovieListViewModel(FakeMovies(fail = true), FakeNotificador())
            advanceUntilIdle()
            assertTrue(vm.uiState.value.movies.isEmpty())
            assertTrue(vm.uiState.value.error != null)
            assertFalse(vm.uiState.value.offline)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun falloConCacheMuestraModoSinConexion() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = MovieListViewModel(FakeMovies(cached = listOf(film), fail = true), FakeNotificador())
            advanceUntilIdle()
            assertEquals(1, vm.uiState.value.visible.size)
            assertTrue(vm.uiState.value.offline)
            assertEquals(null, vm.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun filtroLocalEncuentraTitulo() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = MovieListViewModel(FakeMovies(remote = listOf(film)), FakeNotificador())
            advanceUntilIdle()
            vm.search("inexistente")
            assertTrue(vm.uiState.value.visible.isEmpty())
            vm.search("película")
            assertEquals(1, vm.uiState.value.visible.size)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun ordenarPorNotaNoAlteraFavoritos() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val low = film.copy(id = 12, title = "B", rating = 4.0, favorite = true)
            val vm = MovieListViewModel(FakeMovies(remote = listOf(low, film)), FakeNotificador())
            advanceUntilIdle()
            vm.sort(MovieSort.RATING)
            assertEquals(listOf(11, 12), vm.uiState.value.visible.map { it.id })
            vm.showFavorites(true)
            assertEquals(listOf(12), vm.uiState.value.visible.map { it.id })
        } finally { Dispatchers.resetMain() }
    }
}

private class FakeNotificador : Notificador {
    val avisos = mutableListOf<String>()
    override fun mostrar(titulo: String, texto: String) { avisos += texto }
}

private class FakeMovies(
    cached: List<Movie> = emptyList(),
    private val remote: List<Movie> = emptyList(),
    private val fail: Boolean = false
) : MovieRepository {
    private val items = MutableStateFlow(cached)
    override val movies: Flow<List<Movie>> = items
    override val favorites = items.map { list -> list.filter { it.favorite } }
    override fun movie(id: Int): Flow<Movie?> = items.map { list -> list.firstOrNull { it.id == id } }
    override suspend fun sync(): Int {
        if (fail) throw IllegalStateException("red desconectada")
        items.value = remote
        return remote.size
    }
    override suspend fun refreshDetail(id: Int) = Unit
    override suspend fun setFavorite(id: Int, favorite: Boolean) {
        items.value = items.value.map { if (it.id == id) it.copy(favorite = favorite) else it }
    }
    override suspend fun clearCache() { items.value = emptyList() }
}
