package cl.duoc.app.model.domain

// Modelo de ejemplo para datos externos
// Modifica según la estructura real de la API externa

data class ExternalApiItem(
    val id: Int,
    val title: String, // Título de la historia de terror
    val author: String?, // Autor de la historia (opcional)
    val story: String // Texto completo de la historia
)