package cl.duoc.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import cl.duoc.app.model.domain.BlogCreateUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlogViewModel(private val repo: FormularioBlogsRepository, private val usuarioActual: String) : ViewModel() {

    private val _blogs = MutableStateFlow<List<FormularioBlogsEntity>>(emptyList())
    val blogs: StateFlow<List<FormularioBlogsEntity>> = _blogs.asStateFlow()

    private val _blogCreateState = MutableStateFlow(BlogCreateUIState())
    val blogCreateState: StateFlow<BlogCreateUIState> = _blogCreateState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repo.getBlogs().collectLatest { list -> _blogs.value = list }
            } catch (e: Exception) {
                e.printStackTrace()
                _blogs.value = emptyList()
            }
        }
    }

    // Funciones para actualizar el estado de creación de blogs
    fun onTituloChange(titulo: String) {
        _blogCreateState.update { it.copy(titulo = titulo) }
    }

    fun onDescripcionChange(descripcion: String) {
        _blogCreateState.update { it.copy(descripcion = descripcion) }
    }

    fun onContenidoChange(contenido: String) {
        _blogCreateState.update { it.copy(contenido = contenido) }
    }

    fun onImagenUriChange(uri: Uri?) {
        _blogCreateState.update { it.copy(imagenUri = uri) }
    }

    fun crearBlog() {
        viewModelScope.launch {
            try {
                val state = _blogCreateState.value
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val fecha = dateFormat.format(Date())

                repo.insertarBlog(
                    FormularioBlogsEntity(
                        titulo = state.titulo,
                        descripcion = state.descripcion,
                        contenido = state.contenido,
                        usuarioAutor = usuarioActual,
                        fechaPublicacion = fecha,
                        esPublicado = true,
                        imagenUri = state.imagenUri?.toString()
                    )
                )
                // Limpiar el estado después de crear el blog
                _blogCreateState.value = BlogCreateUIState()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
