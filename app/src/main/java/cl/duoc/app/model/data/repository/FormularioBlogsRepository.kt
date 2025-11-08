package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.FormularioBlogsDao
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import kotlinx.coroutines.flow.Flow

class FormularioBlogsRepository(private val dao: FormularioBlogsDao) {
    
    suspend fun insertarBlog(blog: FormularioBlogsEntity) {
        dao.insertBlog(blog)
    }
    
    suspend fun updateBlog(blog: FormularioBlogsEntity) {
        dao.updateBlog(blog)
    }
    
    suspend fun deleteBlog(blog: FormularioBlogsEntity) {
        dao.deleteBlog(blog)
    }
    
    fun getBlogs(): Flow<List<FormularioBlogsEntity>> {
        return dao.getBlogs()
    }
 
    suspend fun getBlogById(id: Long): FormularioBlogsEntity? {
        return dao.getBlogById(id)
    }
}