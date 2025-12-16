package cl.duoc.app.network

import retrofit2.Response
import retrofit2.http.*
import cl.duoc.app.model.data.entities.NewsEntity

interface NewsApiService {
    @GET("news")
    suspend fun getNews(): Response<List<NewsEntity>>

    @GET("news/{id}")
    suspend fun getNewsById(@Path("id") id: Long): Response<NewsEntity>

    @POST("news")
    suspend fun createNews(@Body news: NewsEntity): Response<NewsEntity>

    @PUT("news/{id}")
    suspend fun updateNews(@Path("id") id: Long, @Body news: NewsEntity): Response<NewsEntity>

    @DELETE("news/{id}")
    suspend fun deleteNews(@Path("id") id: Long): Response<Unit>
}