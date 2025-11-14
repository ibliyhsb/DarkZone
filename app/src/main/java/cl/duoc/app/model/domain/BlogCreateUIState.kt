package cl.duoc.app.model.domain

import android.net.Uri

data class BlogCreateUIState(
    val titulo: String = "",
    val descripcion: String = "",
    val contenido: String = "",
    val imagenUri: Uri? = null
)
