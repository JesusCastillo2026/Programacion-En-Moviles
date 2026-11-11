package com.tecsup.cinecastillo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String,
    val rating: Double,
    val favorite: Boolean,
    val detailLoaded: Boolean
)


