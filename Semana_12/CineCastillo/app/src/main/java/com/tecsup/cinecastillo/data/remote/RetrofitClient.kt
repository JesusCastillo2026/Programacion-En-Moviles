package com.tecsup.cinecastillo.data.remote
import com.tecsup.cinecastillo.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        // La traza de depuración omite parámetros y credenciales; permite verificar rotación y endpoints.
        if (BuildConfig.DEBUG) android.util.Log.i("CineNetwork", "GET " + chain.request().url.encodedPath)
        val url = chain.request().url.newBuilder().addQueryParameter("api_key", BuildConfig.TMDB_API_KEY).build()
        val request = chain.request().newBuilder()
            .url(url)
            .header("Accept", "application/json")
            .build()
        chain.proceed(request)
    }.build()

    val api = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(MovieApi::class.java)

}
