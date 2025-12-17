package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.FormularioUsuarioDao
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity
import cl.duoc.app.network.ApiClient
import cl.duoc.app.network.UserApiService

class FormularioUsuarioRepository(
    private val dao: FormularioUsuarioDao,
    private val userApiService: UserApiService = ApiClient.userApiService
) {

    fun obtenerFormularios() = dao.getFormularios()

    suspend fun guardarFormulario(entity: FormularioUsuarioEntity): String? {
        // La creación de usuario podría seguir siendo local o también puede ser a través del API
        // Por ahora, se mantiene la lógica local.
        if (dao.existeNombreUsuario(entity.nombreUsuario) != null) {
            return "El nombre de usuario ya existe."
        }
        if (dao.existeCorreoUsuario(entity.correoUsuario) > 0) {
            return "El correo electrónico ya está registrado."
        }
        dao.insertarUsuario(entity)
        // También podrías querer llamar a `userApiService.createUser` aquí
        return null
    }

    suspend fun limpiar() = dao.deleteAll()

    suspend fun findByUsernameAndPassword(nombreUsuario: String, password: String): FormularioUsuarioEntity? {
        // El login ya se hace por API en el ViewModel, esta función podría quedar deprecada
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