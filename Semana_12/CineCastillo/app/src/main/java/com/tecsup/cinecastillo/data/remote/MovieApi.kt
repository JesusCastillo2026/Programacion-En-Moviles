package com.tecsup.cinecastillo.data.remote

import com.tecsup.cinecastillo.data.remote.dto.MovieDto
import com.tecsup.cinecastillo.data.remote.dto.MoviePageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("discover/movie")
    suspend fun discover(@Query("language") language: String = "es-PE", @Query("sort_by") sort: String = "popularity.desc", @Query("page") page: Int = 1): MoviePageDto

    @GET("movie/{id}")
    suspend fun detail(@Path("id") id: Int, @Query("language") language: String = "es-PE"): MovieDto

    @GET("search/movie")
    suspend fun search(@Query("query") query: String, @Query("language") language: String = "es-PE"): MoviePageDto
}

