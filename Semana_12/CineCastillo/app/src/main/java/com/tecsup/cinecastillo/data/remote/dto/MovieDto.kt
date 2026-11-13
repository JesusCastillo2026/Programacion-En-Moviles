package com.tecsup.cinecastillo.data.remote.dto

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

