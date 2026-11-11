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

interface MovieRepository {
    val movies: Flow<List<Movie>>
    val favorites: Flow<List<Movie>>
    fun movie(id: Int): Flow<Movie?>
    suspend fun sync(): Int
    suspend fun refreshDetail(id: Int)
    suspend fun setFavorite(id: Int, favorite: Boolean)
    suspend fun clearCache()
}

class RoomMovieRepository(
    private val api: MovieApi,
    private val database: MovieDatabase
) : MovieRepository {
    private val dao = database.movieDao()
    override val movies = dao.observeAll().map { list -> list.map(MovieEntity::toMovie) }
    override val favorites = dao.observeFavorites().map { list -> list.map(MovieEntity::toMovie) }

    override fun movie(id: Int): Flow<Movie?> = dao.observe(id).map { it?.toMovie() }

    override suspend fun sync(): Int {
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
        return remote.size
    }

    override suspend fun refreshDetail(id: Int) {
        val detail = api.detail(id)
        database.withTransaction {
            val old = dao.get(id)
            dao.upsert(listOf(detail.toEntity(favorite = old?.favorite ?: false, detailLoaded = true)))
        }
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean) = dao.setFavorite(id, favorite)
    override suspend fun clearCache() = dao.clear()
}

