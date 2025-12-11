package cl.duoc.app.network

import cl.duoc.app.model.domain.ExternalApiItem
import retrofit2.Call
import retrofit2.http.GET

interface ExternalApiService {
    @GET("external-endpoint") // Reemplaza con el endpoint real
    fun getItems(): Call<List<ExternalApiItem>>
}