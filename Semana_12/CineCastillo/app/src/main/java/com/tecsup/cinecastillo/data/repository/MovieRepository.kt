package com.tecsup.cinecastillo.data.repository
import com.tecsup.cinecastillo.domain.model.Movie
import kotlinx.coroutines.flow.Flow
interface MovieRepository {
    val movies: Flow<List<Movie>>
    val favorites: Flow<List<Movie>>
    fun movie(id: Int): Flow<Movie?>
    suspend fun refrescar(): Result<Int>
    suspend fun obtenerDetalle(id: Int): Result<Movie>
    suspend fun alternarFavorito(id: Int)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()
}
