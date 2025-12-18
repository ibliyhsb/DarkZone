package cl.duoc.app.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class GNewsArticle(
    val title: String,
    val description: String?,
    val url: String,
    val publishedAt: String,
    val source: GNewsSource
)

data class GNewsSource(
    val name: String
)

data class GNewsResponse(
    val articles: List<GNewsArticle>
)

interface GNewsService {
    @GET("search")
    fun getTerrorNews(
        @Query("q") query: String = "terror",
        @Query("lang") lang: String = "es",
        @Query("apikey") apikey: String
    ): Call<GNewsResponse>
}
