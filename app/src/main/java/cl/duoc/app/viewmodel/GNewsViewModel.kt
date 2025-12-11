package cl.duoc.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cl.duoc.app.network.GNewsArticle
import cl.duoc.app.model.repository.GNewsRepository

class GNewsViewModel(apiKey: String) : ViewModel() {
    private val repository = GNewsRepository(apiKey)
    private val _articles = MutableLiveData<List<GNewsArticle>>()
    val articles: LiveData<List<GNewsArticle>> = _articles

    fun fetchTerrorNews() {
        repository.getTerrorNews { result ->
            _articles.postValue(result?.articles ?: emptyList())
        }
    }
}
