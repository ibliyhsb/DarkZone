package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.FormularioComentariosDao
import cl.duoc.app.model.data.entities.FormularioComentariosEntity
import kotlinx.coroutines.flow.Flow

class FormularioComentariosRepository(private val dao: FormularioComentariosDao) {

    suspend fun insertarComentario(comentario: FormularioComentariosEntity) {
        dao.insertComentario(comentario)
    }

    suspend fun updateComentario(comentario: FormularioComentariosEntity) {
        dao.updateComentario(comentario)
    }

    suspend fun deleteComentario(comentario: FormularioComentariosEntity) {
        dao.deleteComentario(comentario)
    }

    fun getComentariosPorBlog(blogId: Long): Flow<List<FormularioComentariosEntity>> {
        return dao.getComentariosByBlog(blogId)
    }
}