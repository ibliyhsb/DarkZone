package cl.duoc.app.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "formulario_blog",
    indices = [Index(value = ["usuario_autor"])]
)
data class FormularioBlogsEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("titulo")
    val titulo: String,
    @SerializedName("descripcion")
    val descripcion: String = "",
    @SerializedName("contenido")
    val contenido: String,
    @ColumnInfo(name = "usuario_autor")
    @SerializedName("usuarioAutor")
    val usuarioAutor: String,
    @SerializedName("fechaPublicacion")
    val fechaPublicacion: String,
    @SerializedName("esPublicado")
    val esPublicado: Boolean = false,
    // URI (String) de imagen opcional seleccionada del dispositivo
    @SerializedName("imagenUri")
    val imagenUri: String? = null
)