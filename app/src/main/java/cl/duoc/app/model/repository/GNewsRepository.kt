package cl.duoc.app.model.repository

import cl.duoc.app.network.GNewsService
import cl.duoc.app.network.GNewsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GNewsRepository(private val apiKey: String) {
    private val apiService: GNewsService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(GNewsService::class.java)
    }

    fun getTerrorNews(callback: (GNewsResponse?) -> Unit) {
        val call = apiService.getTerrorNews(token = apiKey)
        call.enqueue(object : retrofit2.Callback<GNewsResponse> {
            override fun onResponse(
                call: retrofit2.Call<GNewsResponse>,
                response: retrofit2.Response<GNewsResponse>
            ) {
                callback(response.body())
            }

            override fun onFailure(
                call: retrofit2.Call<GNewsResponse>,
                t: Throwable
            ) {
                callback(null)
            }
        })
    }
}
