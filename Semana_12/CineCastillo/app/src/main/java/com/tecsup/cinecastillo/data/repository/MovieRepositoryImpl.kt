package com.tecsup.cinecastillo.data.repository

import androidx.room.withTransaction
import com.tecsup.cinecastillo.data.local.MovieDatabase
import com.tecsup.cinecastillo.data.local.MovieEntity
import com.tecsup.cinecastillo.data.mapper.toEntity
import com.tecsup.cinecastillo.data.mapper.toMovie
import com.tecsup.cinecastillo.data.remote.MovieApi
import com.tecsup.cinecastillo.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomMovieRepository(
    private val api: MovieApi,
    private val database: MovieDatabase
) : MovieRepository {
    private val dao = database.movieDao()
    override val movies = dao.observeAll().map { list -> list.map(MovieEntity::toMovie) }
    override val favorites = dao.observeFavorites().map { list -> list.map(MovieEntity::toMovie) }

    override fun movie(id: Int): Flow<Movie?> = dao.observe(id).map { it?.toMovie() }

    override suspend fun refrescar(): Result<Int> = capturar {
        val remote = api.discover().results
        database.withTransaction {
            val local = dao.getAll().associateBy { it.id }
            val updated = remote.map { dto ->
                dto.toEntity(
                    favorite = local[dto.id]?.favorite ?: false,
                    detailLoaded = local[dto.id]?.detailLoaded ?: false
                ).let { entity ->
                    val old = local[dto.id]
                    if (old?.detailLoaded == true) entity.copy(overview = old.overview) else entity
                }
            }
            dao.upsert(updated)
        }
        remote.size
    }

    override suspend fun obtenerDetalle(id: Int): Result<Movie> = capturar {
        val detail = api.detail(id)
        database.withTransaction {
            val old = dao.get(id)
            dao.upsert(listOf(detail.toEntity(favorite = old?.favorite ?: false, detailLoaded = true)))
        }
        dao.get(id)!!.toMovie()
    }

    override suspend fun alternarFavorito(id: Int) = dao.alternarFavorito(id)
    override suspend fun cantidadGuardada() = dao.contar()
    override suspend fun cantidadFavoritos() = dao.contarFavoritos()
    override suspend fun limpiarCache() = dao.clear()

    private suspend fun <T> capturar(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: kotlinx.coroutines.CancellationException) {
        throw e
    } catch (e: java.io.IOException) {
        Result.failure(e)
    } catch (e: retrofit2.HttpException) {
        Result.failure(e)
    }
}

