package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.app.model.domain.LoginUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow(LoginUIState())
    val estado: StateFlow<LoginUIState> = _estado.asStateFlow()

    fun onNombreChange(v: String) = _estado.update { it.copy(nombreUsuario = v, errores = null) }

    fun onPasswordChange(v: String) = _estado.update { it.copy(passwordUsuario = v, errores = null) }

    fun autenticar(onSuccess: () -> Unit) {
        val s = _estado.value
        if (s.nombreUsuario == "admin" && s.passwordUsuario == "admin") {
            _estado.update { it.copy(errores = null) }
            onSuccess()
        } else {
            _estado.update { it.copy(errores = "Credenciales inválidas") }
        }
    }
}