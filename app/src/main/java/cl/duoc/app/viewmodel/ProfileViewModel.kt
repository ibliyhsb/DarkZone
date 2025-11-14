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

data class ProfileUIState(
    val nombreUsuario: String = "",
    val correoUsuario: String = "",
    val passwordUsuario: String = "",
    val id: Long = 0,
    val updateSuccess: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(private val repository: FormularioUsuarioRepository, private val username: String) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUIState())
    val state: StateFlow<ProfileUIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
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
            try {
                val currentState = _state.value
                val user = FormularioUsuarioEntity(
                    id = currentState.id,
                    nombreUsuario = currentState.nombreUsuario,
                    correoUsuario = currentState.correoUsuario,
                    passwordUsuario = currentState.passwordUsuario
                )
                repository.updateUser(user)
                _state.update { it.copy(updateSuccess = true) }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("UNIQUE constraint failed: formulario_usuario.nombre_usuario") == true -> "El nombre de usuario ya está en uso."
                    e.message?.contains("UNIQUE constraint failed: formulario_usuario.correo_usuario") == true -> "El correo electrónico ya está en uso."
                    else -> "Error al actualizar el perfil."
                }
                _state.update { it.copy(error = errorMessage) }
            }
        }
    }

    fun onSuccessMessageShown() {
        _state.update { it.copy(updateSuccess = false) }
    }
}