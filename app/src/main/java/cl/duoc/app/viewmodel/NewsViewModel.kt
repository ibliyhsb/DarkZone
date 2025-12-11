package cl.duoc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import cl.duoc.app.model.data.entities.NewsEntity
import cl.duoc.app.model.data.repository.NewsRepository
import cl.duoc.app.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _news = MutableStateFlow<List<NewsEntity>>(emptyList())
    val news: StateFlow<List<NewsEntity>> = _news.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // Sincroniza noticias del backend a Room
                val response = withContext(Dispatchers.IO) { ApiClient.newsApiService.getNews() }
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.forEach { repository.insertNews(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            repository.getNews().collect { list ->
                _news.value = list
            }
        }
    }

    suspend fun createNews(news: NewsEntity) {
        try {
            val response = withContext(Dispatchers.IO) { ApiClient.newsApiService.createNews(news) }
            if (response.isSuccessful && response.body() != null) {
                repository.insertNews(response.body()!!)
            } else {
                repository.insertNews(news)
            }
        } catch (e: Exception) {
            repository.insertNews(news)
        }
    }

    suspend fun updateNews(news: NewsEntity) {
        try {
            val response = withContext(Dispatchers.IO) { ApiClient.newsApiService.updateNews(news.id, news) }
            if (response.isSuccessful && response.body() != null) {
                repository.updateNews(response.body()!!)
            } else {
                repository.updateNews(news)
            }
        } catch (e: Exception) {
            repository.updateNews(news)
        }
    }

    suspend fun deleteNews(news: NewsEntity) {
        try {
            val response = withContext(Dispatchers.IO) { ApiClient.newsApiService.deleteNews(news.id) }
            repository.deleteNews(news)
        } catch (e: Exception) {
            repository.deleteNews(news)
        }
    }
}
