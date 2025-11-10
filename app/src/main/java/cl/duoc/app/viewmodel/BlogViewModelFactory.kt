package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.app.model.data.repository.FormularioBlogsRepository

class BlogViewModelFactory(
    private val repo: FormularioBlogsRepository,
    private val usuarioActual: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(repo, usuarioActual) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
