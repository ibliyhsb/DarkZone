package cl.duoc.app.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "formulario_usuario",
    indices = [
        Index(value = ["nombre_usuario"], unique = true),
        Index(value = ["correo_usuario"], unique = true)
    ]
)
data class FormularioUsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Long = 0,

    @ColumnInfo(name = "nombre_usuario")
    @SerializedName("username")
    val nombreUsuario: String,

    @ColumnInfo(name = "correo_usuario")
    @SerializedName("email")
    val correoUsuario: String,

    @SerializedName("password")
    val passwordUsuario: String
)
