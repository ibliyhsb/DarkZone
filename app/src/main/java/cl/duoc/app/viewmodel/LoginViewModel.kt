package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.network.ApiClient
import cl.duoc.app.network.UserApiService
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

class LoginViewModel(private val userApiService: UserApiService = ApiClient.userApiService) : ViewModel() {

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
            try {
                val user = state.value.user
                val pass = state.value.pass
                val credentials = mapOf("username" to user, "password" to pass)
                val response = userApiService.login(credentials)

                if (response.isSuccessful && response.body() != null) {
                    // Aquí puedes manejar la sesión del usuario, por ejemplo, guardando un token
                    _state.update { it.copy(sesionActiva = true, error = null, isLoading = false) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Usuario o contraseña incorrectos"
                    _state.update { it.copy(error = errorBody, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error de conexión: ${e.message}", isLoading = false) }
            }
        }
    }

    fun clearError(){
        _state.update { it.copy(error = null) }
    }
}