package cl.duoc.app.model.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.app.model.data.entities.FormularioComentariosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormularioComentariosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComentario(comentario: FormularioComentariosEntity)

    @Update
    suspend fun updateComentario(comentario: FormularioComentariosEntity)

    @Delete
    suspend fun deleteComentario(comentario: FormularioComentariosEntity)

    @Query("SELECT * FROM formulario_comentarios WHERE blog_id = :blogId ORDER BY fechaCreacion DESC")
    fun getComentariosByBlog(blogId: Long): Flow<List<FormularioComentariosEntity>>
}