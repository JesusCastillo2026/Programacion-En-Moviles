package com.tecsup.lab09.data.remote

import com.tecsup.lab09.data.model.AmphibianModel
import retrofit2.http.GET

interface AmphibianApiService {
    @GET("amphibians")
    suspend fun getAmphibians(): List<AmphibianModel>
}
