package cl.duoc.app.model.domain

data class LoginUIState(
    val nombreUsuario: String = "",
    val passwordUsuario: String = "",
    val errores: String? = null,
    val isLoading: Boolean = false
)
