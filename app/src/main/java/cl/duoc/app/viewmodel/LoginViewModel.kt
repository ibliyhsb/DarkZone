package cl.duoc.app.viewmodel

import androidx.lifecycle.SavedStateHandle
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

class LoginViewModel(private val repository: FormularioUsuarioRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val username: MutableStateFlow<String> = savedStateHandle.get<String>("user")?.let {
        MutableStateFlow(it)
    } ?: MutableStateFlow("")


    init {
        viewModelScope.launch {
            username.collect{
                _state.update {currentState ->
                    currentState.copy(
                        user = it
                    )
                }
            }
        }
    }

    fun onUserChange(user: String) {
        username.value = user
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
                savedStateHandle["user"] = user
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