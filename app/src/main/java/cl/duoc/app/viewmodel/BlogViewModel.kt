package cl.duoc.app.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BlogCreateUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val contenido: String = "",
    val imagenUri: String? = null
)

class BlogViewModel(
    private val repository: FormularioBlogsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val blogs: StateFlow<List<FormularioBlogsEntity>> = repository.getBlogs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _blogCreateState = MutableStateFlow(BlogCreateUiState())
    val blogCreateState = _blogCreateState.asStateFlow()

    private val username: StateFlow<String> = savedStateHandle.getStateFlow("username", "")

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
        _blogCreateState.update { it.copy(imagenUri = uri?.toString()) }
    }

    fun crearBlog() {
        viewModelScope.launch {
            val state = blogCreateState.value
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val formattedDateTime = currentDateTime.format(formatter)

            val blog = FormularioBlogsEntity(
                titulo = state.titulo,
                descripcion = state.descripcion,
                contenido = state.contenido,
                imagenUri = state.imagenUri,
                usuarioAutor = username.value, // Use the logged-in user's name
                fechaPublicacion = formattedDateTime,
                esPublicado = true
            )
            repository.insertarBlog(blog)
            // Reset state after saving
            _blogCreateState.value = BlogCreateUiState()
        }
    }
}
