package cl.duoc.app.model.domain

data class FormularioRegistroErrores(
    val nombreUsuario: String? = null,
    val correoUsuario: String? = null,
    val passwordUsuario: String? = null
){
    fun tieneErrores(): Boolean =
        nombreUsuario != null || correoUsuario != null || passwordUsuario != null
}