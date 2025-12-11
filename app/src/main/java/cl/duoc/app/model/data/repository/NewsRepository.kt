package cl.duoc.app.model.data.repository

import cl.duoc.app.model.data.dao.NewsDao
import cl.duoc.app.model.data.entities.NewsEntity
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val dao: NewsDao) {
    suspend fun insertNews(news: NewsEntity): Long = dao.insertNews(news)
    suspend fun updateNews(news: NewsEntity) = dao.updateNews(news)
    suspend fun deleteNews(news: NewsEntity) = dao.deleteNews(news)
    fun getNews(): Flow<List<NewsEntity>> = dao.getNews()
    suspend fun getNewsById(id: Long): NewsEntity? = dao.getNewsById(id)
}