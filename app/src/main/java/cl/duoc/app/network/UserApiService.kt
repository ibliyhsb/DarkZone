package cl.duoc.app.network

import retrofit2.Response
import retrofit2.http.*
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity

interface UserApiService {
    @GET("/users")
    suspend fun getUsers(): Response<List<FormularioUsuarioEntity>>

    @GET("/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<FormularioUsuarioEntity>

    @POST("/users")
    suspend fun createUser(@Body user: FormularioUsuarioEntity): Response<FormularioUsuarioEntity>

    @PUT("/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: FormularioUsuarioEntity): Response<FormularioUsuarioEntity>

    @DELETE("/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>
}