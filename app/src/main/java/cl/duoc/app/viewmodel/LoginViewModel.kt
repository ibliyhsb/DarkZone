package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository
import cl.duoc.app.model.domain.LoginUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: FormularioUsuarioRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUIState())
    val estado: StateFlow<LoginUIState> = _estado.asStateFlow()

    fun onNombreChange(v: String) = _estado.update { it.copy(nombreUsuario = v, errores = null) }

    fun onPasswordChange(v: String) = _estado.update { it.copy(passwordUsuario = v, errores = null) }

    fun autenticar(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _estado.update { it.copy(isLoading = true) }
            val autenticacion = _estado.value
            val usuario = repository.findByUsernameAndPassword(autenticacion.nombreUsuario, autenticacion.passwordUsuario)

            if (usuario != null) {
                _estado.update { it.copy(errores = null, isLoading = false) }
                onSuccess()
            } else {
                _estado.update { it.copy(errores = "Credenciales inválidas", isLoading = false) }
            }
        }
    }
}