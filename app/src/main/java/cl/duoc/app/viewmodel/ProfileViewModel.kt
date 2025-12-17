package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import cl.duoc.app.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ProfileUIState(
    val nombreUsuario: String = "",
    val correoUsuario: String = "",
    val passwordUsuario: String = "",
    val id: Long = 0,
    val updateSuccess: Boolean = false,
    val error: String? = null,
    val showConfirmDialog: Boolean = false,
    val deleteSuccess: Boolean = false
)

class ProfileViewModel(private val repository: FormularioUsuarioRepository, private val username: String) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUIState())
    val state: StateFlow<ProfileUIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // Sincroniza usuarios del backend a Room
                val response = withContext(Dispatchers.IO) { ApiClient.userApiService.getUsers() }
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.forEach { repository.guardarFormulario(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val user = repository.findByUsername(username)
            user?.let {
                _state.value = ProfileUIState(
                    id = it.id,
                    nombreUsuario = it.nombreUsuario,
                    correoUsuario = it.correoUsuario,
                    passwordUsuario = it.passwordUsuario
                )
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _state.update { it.copy(nombreUsuario = nombre, error = null) }
    }

    fun onCorreoChange(correo: String) {
        _state.update { it.copy(correoUsuario = correo, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(passwordUsuario = password, error = null) }
    }

    fun guardarCambios() {
        viewModelScope.launch {
            _state.update { it.copy(error = null) } // Limpiar errores previos
            try {
                val currentState = _state.value
                val user = FormularioUsuarioEntity(
                    id = currentState.id,
                    nombreUsuario = currentState.nombreUsuario,
                    correoUsuario = currentState.correoUsuario,
                    passwordUsuario = currentState.passwordUsuario
                )
                // Actualiza en backend
                val response = withContext(Dispatchers.IO) { ApiClient.userApiService.updateUser(user.id, user) }

                if (response.isSuccessful && response.body() != null) {
                    // Si el backend actualizó, actualizar el repositorio local y notificar éxito
                    repository.updateUser(response.body()!!)
                    _state.update { it.copy(updateSuccess = true) }
                } else {
                    // Si el backend falló, mostrar el error
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al actualizar."
                    _state.update { it.copy(error = errorBody) }
                }
            } catch (e: Exception) {
                // Capturar errores de conexión u otros
                val errorMessage = when {
                    e.message?.contains("UNIQUE constraint failed: formulario_usuario.nombre_usuario") == true -> "El nombre de usuario ya está en uso."
                    e.message?.contains("UNIQUE constraint failed: formulario_usuario.correo_usuario") == true -> "El correo electrónico ya está en uso."
                    else -> "Error de conexión: ${e.message}"
                }
                _state.update { it.copy(error = errorMessage) }
            }
        }
    }

    fun onSuccessMessageShown() {
        _state.update { it.copy(updateSuccess = false) }
    }

    fun onDeleteRequest() {
        _state.update { it.copy(showConfirmDialog = true) }
    }

    fun onDeleteConfirm() {
        viewModelScope.launch {
            val user = FormularioUsuarioEntity(
                id = _state.value.id,
                nombreUsuario = _state.value.nombreUsuario,
                correoUsuario = _state.value.correoUsuario,
                passwordUsuario = _state.value.passwordUsuario
            )
            try {
                val response = withContext(Dispatchers.IO) { ApiClient.userApiService.deleteUser(user.id) }
                // Elimina local siempre
                repository.deleteUser(user)
                _state.update { it.copy(deleteSuccess = true, showConfirmDialog = false) }
            } catch (e: Exception) {
                repository.deleteUser(user)
                _state.update { it.copy(deleteSuccess = true, showConfirmDialog = false) }
            }
        }
    }

    fun onDeleteCancel() {
        _state.update { it.copy(showConfirmDialog = false) }
    }
}