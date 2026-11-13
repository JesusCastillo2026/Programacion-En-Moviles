package com.tecsup.cinecastillo.data.mapper

import com.tecsup.cinecastillo.data.local.MovieEntity
import com.tecsup.cinecastillo.data.remote.dto.MovieDto
import com.tecsup.cinecastillo.domain.model.Movie

/** Mantiene separados los formatos de la API, de Room y de la interfaz. */
fun MovieDto.toEntity(favorite: Boolean = false, detailLoaded: Boolean = false) = MovieEntity(
    id = id,
    title = title.orEmpty(),
    overview = overview.orEmpty(),
    posterPath = posterPath,
    releaseDate = releaseDate.orEmpty(),
    rating = rating ?: 0.0,
    favorite = favorite,
    detailLoaded = detailLoaded
)

fun MovieEntity.toMovie() = Movie(id, title, overview, posterPath, releaseDate, rating, favorite, detailLoaded)
