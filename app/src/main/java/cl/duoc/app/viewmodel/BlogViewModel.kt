package cl.duoc.app.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.repository.FormularioBlogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BlogViewModel(private val repo: FormularioBlogsRepository, private val usuarioActual: String) : ViewModel() {

    private val _blogs = MutableStateFlow<List<FormularioBlogsEntity>>(emptyList())
    val blogs: StateFlow<List<FormularioBlogsEntity>> = _blogs.asStateFlow()

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

    fun crearBlog(
        titulo: String,
        descripcion: String,
        contenido: String,
        imagenUri: String?
    ) {
        viewModelScope.launch {
            try {
                val fecha = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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
            } catch (e: SQLiteConstraintException) {
                // Manejar la excepción, por ejemplo, mostrando un mensaje al usuario.
                // Por ahora, simplemente la ignoramos para que no se caiga la app.
            }
        }
    }
}
