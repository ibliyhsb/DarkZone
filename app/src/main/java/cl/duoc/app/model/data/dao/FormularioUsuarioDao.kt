package cl.duoc.app.model.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormularioUsuarioDao {
    @Query("SELECT * FROM formulario_usuario WHERE nombre_usuario = :nombreUsuario LIMIT 1")
    suspend fun existeNombreUsuario(nombreUsuario: String): FormularioUsuarioEntity?

    @Query("SELECT COUNT(*) FROM formulario_usuario WHERE correo_usuario = :correoUsuario")
    suspend fun existeCorreoUsuario(correoUsuario: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: FormularioUsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertFormulario(formulario: FormularioUsuarioEntity): Long

    @Query("SELECT * FROM formulario_usuario ORDER BY id DESC")
    fun getFormularios(): Flow<List<FormularioUsuarioEntity>>

    @Query("DELETE FROM formulario_usuario")
    suspend fun deleteAll()

}