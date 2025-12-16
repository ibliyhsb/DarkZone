package cl.duoc.app.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import cl.duoc.app.model.data.repository.FormularioBlogsRepository

class BlogViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: FormularioBlogsRepository,
    private val nombreUsuario: String = "usuario_demo",
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(repository, nombreUsuario) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}