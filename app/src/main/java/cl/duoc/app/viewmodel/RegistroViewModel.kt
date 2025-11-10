package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository
import cl.duoc.app.model.domain.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(private val registroRepository: FormularioUsuarioRepository) : ViewModel(){

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String){
        _estado.update { actual ->
            actual.copy(
                nombreUsuario = valor,
                errores = actual.errores.copy(
                    nombreUsuario = if(valor.isBlank()) "El nombre de usuario es requerido" else null
                )
            )
        }
    }

    fun onCorreoChange(valor: String){
        _estado.update { actual ->
            actual.copy(
                correoUsuario = valor,
                errores = actual.errores.copy(
                    correoUsuario = when {
                        valor.isBlank() -> "El correo es requerido"
                        !EMAIL_REGEX.matches(valor) -> "El formato del correo no es valido"
                        else -> null
                    }
                )
            )
        }
    }

    fun onContrasenaChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                passwordUsuario = valor,
                errores = actual.errores.copy(
                    passwordUsuario = when {
                        valor.isBlank() -> "La contraseña es requerida"
                        valor.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
                        else -> null
                    }
                )
            )
        }
    }

    fun onEnviarFormulario(){
        val ui = _estado.value

        val errores = ui.errores.copy(
            nombreUsuario = if(ui.nombreUsuario.isBlank()) "El nombre de usuario es requerido" else null,
            correoUsuario = if (ui.correoUsuario.isBlank()) "El correo es requerido" else null,
            passwordUsuario = if (ui.passwordUsuario.isBlank()) "La contraseña es requerida" else null
        )

        _estado.update { it.copy(errores = errores) }

        if(errores.tieneErrores()) return

        viewModelScope.launch {
            val entity = FormularioUsuarioEntity(
                nombreUsuario = ui.nombreUsuario,
                correoUsuario = ui.correoUsuario,
                passwordUsuario = ui.passwordUsuario
            )
            val mensajeError = registroRepository.guardarFormulario(entity)

            if (mensajeError != null) {
                _estado.update { actual ->
                    actual.copy(
                        errores = actual.errores.copy(
                            nombreUsuario = if (mensajeError.contains("nombre de usuario")) mensajeError else null,
                            correoUsuario = if (mensajeError.contains("correo")) mensajeError else null
                        )
                    )
                }
            } else {
                _estado.update { RegistroUiState() }
            }
        }
    }

    companion object{
        private val EMAIL_REGEX =
          "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }
}