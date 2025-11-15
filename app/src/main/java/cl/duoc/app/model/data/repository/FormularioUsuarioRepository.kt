package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.FormularioUsuarioDao
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity

class FormularioUsuarioRepository(private val dao: FormularioUsuarioDao) {

    fun obtenerFormularios() = dao.getFormularios()

    suspend fun guardarFormulario(entity: FormularioUsuarioEntity): String? {
        if (dao.existeNombreUsuario(entity.nombreUsuario) != null) {
            return "El nombre de usuario ya existe."
        }
        if (dao.existeCorreoUsuario(entity.correoUsuario) > 0) {
            return "El correo electrónico ya está registrado."
        }
        dao.insertarUsuario(entity)
        return null
    }

    suspend fun limpiar() = dao.deleteAll()

    suspend fun findByUsernameAndPassword(nombreUsuario: String, password: String): FormularioUsuarioEntity? {
        return dao.findByUsernameAndPassword(nombreUsuario, password)
    }

    suspend fun findByUsername(nombreUsuario: String): FormularioUsuarioEntity? {
        return dao.existeNombreUsuario(nombreUsuario)
    }

    suspend fun updateUser(usuario: FormularioUsuarioEntity) {
        dao.updateUser(usuario)
    }

    suspend fun deleteUser(usuario: FormularioUsuarioEntity) {
        dao.deleteUser(usuario)
    }
}