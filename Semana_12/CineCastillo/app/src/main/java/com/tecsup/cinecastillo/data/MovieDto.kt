package com.tecsup.cinecastillo.data

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val rating: Double?
)

data class MoviePageDto(val results: List<MovieDto>)

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
