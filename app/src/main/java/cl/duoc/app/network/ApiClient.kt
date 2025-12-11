package cl.duoc.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // Cambia esto a la URL de tu backend

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val blogApiService: BlogApiService = retrofit.create(BlogApiService::class.java)
    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)
    val newsApiService: NewsApiService = retrofit.create(NewsApiService::class.java)
}