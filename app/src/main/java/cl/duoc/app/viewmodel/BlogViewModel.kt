package cl.duoc.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.BlogCreateDto
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import cl.duoc.app.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// State for the blog creation screen
data class BlogCreateState(
    val titulo: String = "",
    val descripcion: String = "",
    val contenido: String = "",
    val imagenUri: Uri? = null
)

class BlogViewModel(private val repo: FormularioBlogsRepository, val usuarioActual: String) : ViewModel() {

    private val _blogs = MutableStateFlow<List<FormularioBlogsEntity>>(emptyList())
    val blogs: StateFlow<List<FormularioBlogsEntity>> = _blogs.asStateFlow()

    // Selected blog para edición/visualización individual
    private val _selectedBlog = MutableStateFlow<FormularioBlogsEntity?>(null)
    val selectedBlog: StateFlow<FormularioBlogsEntity?> = _selectedBlog.asStateFlow()

    // Recently viewed blogs (in-memory)
    private val _recentBlogs = MutableStateFlow<List<FormularioBlogsEntity>>(emptyList())
    val recentBlogs: StateFlow<List<FormularioBlogsEntity>> = _recentBlogs.asStateFlow()

    // State for blog creation
    private val _blogCreateState = MutableStateFlow(BlogCreateState())
    val blogCreateState: StateFlow<BlogCreateState> = _blogCreateState.asStateFlow()


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
                // 1. Intenta cargar del backend y guardar en Room
                val response = withContext(Dispatchers.IO) { ApiClient.blogApiService.getBlogs() }
                if (response.isSuccessful && response.body() != null) {
                    val remoteBlogs = response.body()!!
                    // Limpia y guarda en Room
                    remoteBlogs.forEach { repo.insertarBlog(it) }
                }
            } catch (e: Exception) {
                Log.e("BlogViewModel", "Error sincronizando con backend", e)
            }
            // Siempre observa Room
            try {
                repo.getBlogs().collectLatest { list ->
                    if (list.isEmpty()) {
                        _blogs.value = sampleBlogs()
                    } else {
                        _blogs.value = list
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _blogs.value = sampleBlogs()
            }
        }
    }

    fun onTituloChange(titulo: String) {
        _blogCreateState.value = _blogCreateState.value.copy(titulo = titulo)
    }

    fun onDescripcionChange(descripcion: String) {
        _blogCreateState.value = _blogCreateState.value.copy(descripcion = descripcion)
    }

    fun onContenidoChange(contenido: String) {
        _blogCreateState.value = _blogCreateState.value.copy(contenido = contenido)
    }

    fun onImagenUriChange(uri: Uri?) {
        _blogCreateState.value = _blogCreateState.value.copy(imagenUri = uri)
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

    fun crearBlog() {
        val currentState = _blogCreateState.value
        if (currentState.titulo.isBlank() || currentState.contenido.isBlank()) {
            return
        }
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val fecha = dateFormat.format(Date())
                
                // Crear DTO sin el campo id
                val blogDto = BlogCreateDto(
                    titulo = currentState.titulo,
                    descripcion = currentState.descripcion,
                    contenido = currentState.contenido,
                    usuarioAutor = usuarioActual,
                    fechaPublicacion = fecha,
                    esPublicado = true,
                    imagenUri = currentState.imagenUri?.toString()
                )
                
                // 1. Crea en backend
                val response = withContext(Dispatchers.IO) { ApiClient.blogApiService.createBlog(blogDto) }
                if (response.isSuccessful && response.body() != null) {
                    // 2. Guarda en Room con el id del backend
                    repo.insertarBlog(response.body()!!)
                    Log.d("BlogViewModel", "Blog guardado en backend Y localmente titulo=${currentState.titulo}")
                } else {
                    Log.e("BlogViewModel", "Error del backend: ${response.code()} - ${response.message()}")
                    // Si falla el backend, crear entidad local
                    val localBlog = FormularioBlogsEntity(
                        titulo = currentState.titulo,
                        descripcion = currentState.descripcion,
                        contenido = currentState.contenido,
                        usuarioAutor = usuarioActual,
                        fechaPublicacion = blogDto.fechaPublicacion,
                        esPublicado = true,
                        imagenUri = currentState.imagenUri?.toString()
                    )
                    repo.insertarBlog(localBlog)
                    Log.d("BlogViewModel", "Blog guardado SOLO localmente (backend falló)")
                }
                _blogCreateState.value = BlogCreateState()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSelectedBlog(titulo: String, descripcion: String, contenido: String, imagenUri: String?) {
        val current = _selectedBlog.value ?: return
        if (current.usuarioAutor != usuarioActual) return
        val updated = current.copy(
            titulo = titulo,
            descripcion = descripcion,
            contenido = contenido,
            imagenUri = imagenUri
        )
        viewModelScope.launch {
            try {
                // 1. Actualiza en backend
                val response = withContext(Dispatchers.IO) { ApiClient.blogApiService.updateBlog(updated.id, updated) }
                if (response.isSuccessful && response.body() != null) {
                    repo.updateBlog(response.body()!!)
                } else {
                    repo.updateBlog(updated)
                }
                _selectedBlog.value = updated
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteSelectedBlog(onDeleted: (() -> Unit)? = null) {
        val current = _selectedBlog.value ?: return
        if (current.usuarioAutor != usuarioActual) return
        viewModelScope.launch {
            try {
                // 1. Elimina en backend
                val response = withContext(Dispatchers.IO) { ApiClient.blogApiService.deleteBlog(current.id) }
                // 2. Elimina local siempre
                repo.deleteBlog(current)
                _selectedBlog.value = null
                onDeleted?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
