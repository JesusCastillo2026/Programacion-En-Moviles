package com.tecsup.cinecastillo

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.cinecastillo.data.auth.FirebaseAuthRepository
import com.tecsup.cinecastillo.data.remote.MovieApi
import com.tecsup.cinecastillo.data.repository.RoomMovieRepository
import com.tecsup.cinecastillo.notification.NotificadorAndroid
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    val notificador = NotificadorAndroid(context.applicationContext)
    private val database = Room.databaseBuilder(
        context.applicationContext,
        com.tecsup.cinecastillo.data.local.MovieDatabase::class.java,
        "cine_castillo.db"
    ).build()

    private val api = com.tecsup.cinecastillo.data.remote.RetrofitClient.api

    val movies = RoomMovieRepository(api, database)
    val auth by lazy {
        FirebaseAuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()) {
            val manager = context.applicationContext.getSystemService(android.net.ConnectivityManager::class.java)
            val network = manager.activeNetwork
            val capabilities = manager.getNetworkCapabilities(network)
            capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
    }
}

