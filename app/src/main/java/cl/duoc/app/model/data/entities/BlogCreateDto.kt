package cl.duoc.app.model.data.entities

import com.google.gson.annotations.SerializedName

/**
 * DTO para crear un blog nuevo (sin campo id)
 */
data class BlogCreateDto(
    @SerializedName("titulo")
    val titulo: String,
    @SerializedName("descripcion")
    val descripcion: String = "",
    @SerializedName("contenido")
    val contenido: String,
    @SerializedName("usuarioAutor")
    val usuarioAutor: String,
    @SerializedName("fechaPublicacion")
    val fechaPublicacion: String,
    @SerializedName("esPublicado")
    val esPublicado: Boolean = false,
    @SerializedName("imagenUri")
    val imagenUri: String? = null
)
