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

    // Recently viewed blogs (in-memory)
    private val _recentBlogs = MutableStateFlow<List<FormularioBlogsEntity>>(emptyList())
    val recentBlogs: StateFlow<List<FormularioBlogsEntity>> = _recentBlogs.asStateFlow()

    // Sample (ejemplo) blogs to show when DB is empty
    private fun sampleBlogs(): List<FormularioBlogsEntity> = listOf(
        FormularioBlogsEntity(
            id = -1,
            titulo = "Guía rápida de DarkZone",
            descripcion = "Introducción y primeros pasos",
            contenido = "Bienvenido a DarkZone...",
            usuarioAutor = "admin",
            fechaPublicacion = "2025-11-10",
            esPublicado = true,
            imagenUri = null
        ),
        FormularioBlogsEntity(
            id = -2,
            titulo = "Consejos de seguridad",
            descripcion = "Mejora tu privacidad",
            contenido = "Consejos y trucos para...",
            usuarioAutor = "moderador",
            fechaPublicacion = "2025-11-09",
            esPublicado = true,
            imagenUri = null
        )
    )

    init {
        viewModelScope.launch {
            try {
                repo.getBlogs().collectLatest { list ->
                    if (list.isEmpty()) {
                        // Si la DB está vacía, mostrar ejemplos en memoria
                        _blogs.value = sampleBlogs()
                    } else {
                        _blogs.value = list
                    }
                }
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
                // Preferir blog desde repo si existe, sino desde lista en memoria
                val fromDb = repo.getBlogById(id)
                _selectedBlog.value = fromDb ?: _blogs.value.firstOrNull { it.id == id }
            } catch (e: Exception) {
                e.printStackTrace()
                _selectedBlog.value = null
            }
        }
    }

    fun markBlogAsViewed(id: Long) {
        val blog = _blogs.value.firstOrNull { it.id == id } ?: return
        val updated = listOf(blog) + _recentBlogs.value.filter { it.id != id }
        _recentBlogs.value = updated.take(10)
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
