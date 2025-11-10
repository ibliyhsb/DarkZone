package cl.duoc.app.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "formulario_comentarios",
    foreignKeys = [
        ForeignKey(
            entity = FormularioUsuarioEntity::class,
            parentColumns = ["nombre_usuario"],
            childColumns = ["nombre_usuario"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = FormularioBlogsEntity::class,
            parentColumns = ["id"],
            childColumns = ["blog_id"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["nombre_usuario"]), Index(value = ["blog_id"])]
)
data class FormularioComentariosEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "nombre_usuario")
    val nombreUsuario: String,
    @ColumnInfo(name = "blog_id")
    val blogId: Long,
    val comentario: String,
    val fechaCreacion: String
)