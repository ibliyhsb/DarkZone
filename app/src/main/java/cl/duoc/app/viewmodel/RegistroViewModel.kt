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
import cl.duoc.app.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    fun onPasswordChange(valor: String) {
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

        _estado.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // Crear un map sin el campo id para que el backend genere el ID
                val userDto = mapOf(
                    "nombreUsuario" to ui.nombreUsuario,
                    "correoUsuario" to ui.correoUsuario,
                    "passwordUsuario" to ui.passwordUsuario
                )
                
                android.util.Log.d("RegistroViewModel", "Intentando conectar al backend...")
                
                // Intenta crear en backend
                val response = withContext(Dispatchers.IO) { 
                    ApiClient.userApiService.createUser(userDto) 
                }
                
                if (response.isSuccessful && response.body() != null) {
                    // Backend respondió correctamente - guarda en Room
                    val errorMessage = registroRepository.guardarFormulario(response.body()!!)
                    
                    if (errorMessage == null) {
                        android.util.Log.d("RegistroViewModel", "Usuario guardado en backend Y base de datos local")
                        _estado.update { it.copy(
                            registroExitoso = true, 
                            isLoading = false,
                            mensajeExito = "Usuario registrado en servidor y guardado localmente"
                        )}
                    } else {
                        _estado.update { it.copy(isLoading = false) }
                        if (errorMessage.contains("correo")) {
                            _estado.update { it.copy(errores = it.errores.copy(correoUsuario = errorMessage)) }
                        } else {
                            _estado.update { it.copy(errores = it.errores.copy(nombreUsuario = errorMessage)) }
                        }
                    }
                } else {
                    // Backend respondió con error
                    android.util.Log.w("RegistroViewModel", "Backend error: ${response.code()} - ${response.message()}")
                    _estado.update { it.copy(
                        isLoading = false,
                        errores = it.errores.copy(
                            nombreUsuario = "Error del servidor (código ${response.code()}). Servicios backend no disponibles."
                        )
                    )}
                }
            } catch (e: Exception) {
                // Error de red o conexión - NO GUARDAMOS LOCALMENTE
                android.util.Log.e("RegistroViewModel", "Error de conexión: ${e.message}", e)
                
                _estado.update { it.copy(
                    isLoading = false,
                    errores = it.errores.copy(
                        nombreUsuario = "No se puede conectar al servidor. Verifica que los servicios backend estén corriendo en localhost:8083"
                    )
                )}
            }
        }
    }

    fun onMensajeExitosoMostrado() {
        _estado.update { RegistroUiState() }
    }

    companion object{
        private val EMAIL_REGEX =
          "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }
}