package cl.duoc.app.model.repository

import cl.duoc.app.model.domain.ExternalApiItem
import cl.duoc.app.network.ExternalApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExternalApiRepository {
    private val apiService: ExternalApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.externa.com/") // Reemplaza con la URL real
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ExternalApiService::class.java)
    }

    fun getItems(callback: (List<ExternalApiItem>?) -> Unit) {
        val call = apiService.getItems()
        call.enqueue(object : retrofit2.Callback<List<ExternalApiItem>> {
            override fun onResponse(
                call: retrofit2.Call<List<ExternalApiItem>>,
                response: retrofit2.Response<List<ExternalApiItem>>
            ) {
                callback(response.body())
            }

            override fun onFailure(
                call: retrofit2.Call<List<ExternalApiItem>>,
                t: Throwable
            ) {
                callback(null)
            }
        })
    }
}
