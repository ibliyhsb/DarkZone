package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class Post(val id: Long, val title: String, val author: String)

class StartViewModel(blogsRepository: FormularioBlogsRepository) : ViewModel() {

    val blogs: StateFlow<List<Post>> = blogsRepository.getBlogs()
        .map {
            it.map { blog -> Post(blog.id, blog.titulo, blog.usuarioAutor) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val newsViewModel = NewsViewModel()
    val news: StateFlow<List<Post>> = newsViewModel.articles
        .map {
            it.map { article -> Post(article.id.toLong(), article.title, article.source) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}