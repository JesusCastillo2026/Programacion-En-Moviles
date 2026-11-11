package com.tecsup.cinecastillo

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.cinecastillo.data.FirebaseAuthRepository
import com.tecsup.cinecastillo.data.MovieApi
import com.tecsup.cinecastillo.data.RoomMovieRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        com.tecsup.cinecastillo.data.MovieDatabase::class.java,
        "cine_castillo.db"
    ).build()

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val url = chain.request().url.newBuilder().addQueryParameter("api_key", BuildConfig.TMDB_API_KEY).build()
        val request = chain.request().newBuilder()
            .url(url)
            .header("Accept", "application/json")
            .build()
        chain.proceed(request)
    }.build()

    private val api = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(MovieApi::class.java)

    val movies = RoomMovieRepository(api, database)
    val auth by lazy { FirebaseAuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()) }
}
