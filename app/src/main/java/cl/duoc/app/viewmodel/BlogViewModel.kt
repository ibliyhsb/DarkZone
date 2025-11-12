package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import android.util.Log
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
                    Log.d("BlogViewModel", "getBlogs emitted size=${list.size} ids=${list.map { it.id }}")
                    if (list.isEmpty()) {
                        // Si la DB está vacía, mostrar ejemplos en memoria
                        _blogs.value = sampleBlogs()
                        Log.d("BlogViewModel", "Using sample blogs: ${_blogs.value.map { it.id }}")
                    } else {
                        _blogs.value = list
                    }
                }
            } catch (e: Exception) {
                // Log the error or handle it appropriately
                e.printStackTrace()
                _blogs.value = sampleBlogs() // Muestra los blogs de ejemplo si hay un error
            }
        }
    }

    fun loadBlogById(id: Long) {
        viewModelScope.launch {
            if (id < 0) { // Es un blog de ejemplo (ID negativo)
                // Para blogs de ejemplo, buscar directamente en la función que los crea.
                // Esto evita problemas si la lista principal (_blogs) aún no se ha cargado.
                _selectedBlog.value = sampleBlogs().firstOrNull { it.id == id }
            } else { // Blog real de la base de datos
                try {
                    // Para blogs reales, buscar en la base de datos.
                    val fromDb = repo.getBlogById(id)
                    _selectedBlog.value = fromDb
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Si hay un error, como fallback, intentar buscar en la lista en memoria.
                    _selectedBlog.value = blogs.value.firstOrNull { it.id == id }
                }
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
                val newId = repo.insertarBlog(
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
                Log.d("BlogViewModel", "Inserted blog id=$newId titulo=$titulo")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
