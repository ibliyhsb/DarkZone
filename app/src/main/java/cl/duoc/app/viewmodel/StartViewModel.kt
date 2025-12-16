package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import cl.duoc.app.model.data.repository.NewsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class Post(val id: Long, val title: String, val author: String)

class StartViewModel(
    blogsRepository: FormularioBlogsRepository,
    newsRepository: NewsRepository
) : ViewModel() {

    val blogs: StateFlow<List<Post>> = blogsRepository.getBlogs()
        .map {
            it.map { blog -> Post(blog.id, blog.titulo, blog.usuarioAutor) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val news: StateFlow<List<Post>> = newsRepository.getNews()
        .map {
            it.map { article -> Post(article.id, article.titulo, article.autor) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}