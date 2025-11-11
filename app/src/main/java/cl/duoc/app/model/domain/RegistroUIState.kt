package cl.duoc.app.model.domain

    data class RegistroUiState(
        val nombreUsuario: String = "",
        val correoUsuario: String = "",
        val passwordUsuario: String = "",
        val errores: FormularioRegistroErrores = FormularioRegistroErrores(),
        val registroExitoso: Boolean = false
    )
