package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val user: String = "",
    val pass: String = "",
    val sesionActiva: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(private val repository: FormularioUsuarioRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onUserChange(user: String) {
        _state.update { it.copy(user = user) }
    }

    fun onPassChange(pass: String) {
        _state.update { it.copy(pass = pass) }
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = state.value.user
            val pass = state.value.pass
            val usuario = repository.findByUsernameAndPassword(user, pass)

            if (usuario != null) {
                _state.update { it.copy(sesionActiva = true, error = null, isLoading = false) }
            } else {
                _state.update { it.copy(error = "Usuario o contraseña incorrectos", isLoading = false) }
            }
        }
    }

    fun clearError(){
        _state.update { it.copy(error = null) }
    }
}