package cl.duoc.app.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "formulario_blog",
    indices = [Index(value = ["usuario_autor"])]
)
data class FormularioBlogsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    val contenido: String,
    @ColumnInfo(name = "usuario_autor")
    val usuarioAutor: String,
    val fechaPublicacion: String,
    val esPublicado: Boolean = false,
    // URI (String) de imagen opcional seleccionada del dispositivo
    val imagenUri: String? = null
)