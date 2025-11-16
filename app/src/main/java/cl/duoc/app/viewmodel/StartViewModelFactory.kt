package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.app.model.data.repository.FormularioBlogsRepository

class StartViewModelFactory(private val repository: FormularioBlogsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}