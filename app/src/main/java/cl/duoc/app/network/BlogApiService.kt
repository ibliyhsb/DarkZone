package cl.duoc.app.network

import retrofit2.Response
import retrofit2.http.*
import cl.duoc.app.model.data.entities.FormularioBlogsEntity

interface BlogApiService {
    @GET("/blogs")
    suspend fun getBlogs(): Response<List<FormularioBlogsEntity>>

    @GET("/blogs/{id}")
    suspend fun getBlogById(@Path("id") id: Long): Response<FormularioBlogsEntity>

    @POST("/blogs")
    suspend fun createBlog(@Body blog: FormularioBlogsEntity): Response<FormularioBlogsEntity>

    @PUT("/blogs/{id}")
    suspend fun updateBlog(@Path("id") id: Long, @Body blog: FormularioBlogsEntity): Response<FormularioBlogsEntity>

    @DELETE("/blogs/{id}")
    suspend fun deleteBlog(@Path("id") id: Long): Response<Unit>
}