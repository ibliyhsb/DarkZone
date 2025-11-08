package cl.duoc.app.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "formulario_blog",
    foreignKeys = [
        ForeignKey(
            entity = FormularioUsuarioEntity::class,
            parentColumns = ["nombre_usuario"],
            childColumns = ["usuario_autor"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["usuario_autor"])]
)
data class FormularioBlogsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val contenido: String,
    @ColumnInfo(name = "usuario_autor")
    val usuarioAutor: String,
    val fechaPublicacion: String,
    val esPublicado: Boolean = false
)