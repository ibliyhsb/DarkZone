package cl.duoc.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.duoc.app.model.domain.ExternalApiItem
import cl.duoc.app.model.repository.ExternalApiRepository

class ExternalApiViewModel : ViewModel() {
    private val repository = ExternalApiRepository()
    private val _items = MutableLiveData<List<ExternalApiItem>>()
    val items: LiveData<List<ExternalApiItem>> = _items

    fun fetchItems() {
        repository.getItems { result ->
            _items.postValue(result ?: emptyList())
        }
    }
}
