package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlogViewModel(private val repo: FormularioBlogsRepository, private val usuarioActual: String) : ViewModel() {

    private val _blogs = MutableStateFlow<List<FormularioBlogsEntity>>(emptyList())
    val blogs: StateFlow<List<FormularioBlogsEntity>> = _blogs.asStateFlow()

    // Selected blog para edición/visualización individual
    private val _selectedBlog = MutableStateFlow<FormularioBlogsEntity?>(null)
    val selectedBlog: StateFlow<FormularioBlogsEntity?> = _selectedBlog.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repo.getBlogs().collectLatest { list -> _blogs.value = list }
            } catch (e: Exception) {
                // Log the error or handle it appropriately
                e.printStackTrace()
                _blogs.value = emptyList()
            }
        }
    }

    fun loadBlogById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedBlog.value = repo.getBlogById(id)
            } catch (e: Exception) {
                e.printStackTrace()
                _selectedBlog.value = null
            }
        }
    }

    fun crearBlog(
        titulo: String,
        descripcion: String,
        contenido: String,
        imagenUri: String?
    ) {
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val fecha = dateFormat.format(Date())
                repo.insertarBlog(
                    FormularioBlogsEntity(
                        titulo = titulo,
                        descripcion = descripcion,
                        contenido = contenido,
                        usuarioAutor = usuarioActual,
                        fechaPublicacion = fecha,
                        esPublicado = true,
                        imagenUri = imagenUri
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
