package cl.duoc.app.model.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormularioBlogsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlog(blog: FormularioBlogsEntity): Long

    @Update
    suspend fun updateBlog(blog: FormularioBlogsEntity)

    @Delete
    suspend fun deleteBlog(blog: FormularioBlogsEntity)

    @Query("SELECT id, titulo, descripcion, contenido, usuario_autor, fechaPublicacion, esPublicado, imagenUri FROM formulario_blog ORDER BY fechaPublicacion DESC")
    fun getBlogs(): Flow<List<FormularioBlogsEntity>>

    @Query("SELECT id, titulo, descripcion, contenido, usuario_autor, fechaPublicacion, esPublicado, imagenUri FROM formulario_blog WHERE id = :id")
    suspend fun getBlogById(id: Long): FormularioBlogsEntity?
}