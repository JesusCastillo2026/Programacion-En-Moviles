package com.tecsup.cinecastillo.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String,
    val rating: Double,
    val favorite: Boolean = false,
    val detailLoaded: Boolean = false
) {
    val posterUrl: String? get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
}

