package cl.duoc.app.model.data.entities

data class NewsEntity(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    val contenido: String,
    val autor: String,
    val fechaPublicacion: String,
    val esPublicado: Boolean = false,
    val imagenUri: String? = null
)