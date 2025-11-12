package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Simple data model for an article
data class NewsArticle(
    val id: Int,
    val title: String,
    val source: String,
    val content: String,
    val url: String?
)

class NewsViewModel : ViewModel() {

    private val _articles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val articles: StateFlow<List<NewsArticle>> = _articles.asStateFlow()

    init {
        // Load mock data for now. This can be replaced with a network call later.
        viewModelScope.launch {
            _articles.value = listOf(
                NewsArticle(0, "La casa embrujada: nueva adaptación cinematográfica", "Fuente CineTerror", "Detalle de la noticia 1...", "https://example.com/nota1"),
                NewsArticle(1, "Antología de cuentos góticos reeditada", "Fuente Literatura", "Detalle de la noticia 2...", "https://example.com/nota2"),
                NewsArticle(2, "Festival de cine de terror abre sus puertas", "Fuente Eventos", "Detalle de la noticia 3...", null)
            )
        }
    }

    fun getArticleById(id: Int?): NewsArticle? = articles.value.firstOrNull { it.id == id }
}
