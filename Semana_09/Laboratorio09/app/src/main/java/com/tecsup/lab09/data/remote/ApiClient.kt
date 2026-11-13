package com.tecsup.lab09.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private const val AMPHIBIANS_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

    val service: PostApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java)
    }

    val amphibiansService: AmphibianApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AMPHIBIANS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AmphibianApiService::class.java)
    }
}
