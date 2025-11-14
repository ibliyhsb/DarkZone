package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.app.model.data.repository.FormularioUsuarioRepository

class ProfileViewModelFactory(private val repository: FormularioUsuarioRepository, private val username: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository, username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}