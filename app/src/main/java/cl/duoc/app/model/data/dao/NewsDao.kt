package cl.duoc.app.model.data.dao

import androidx.room.*
import cl.duoc.app.model.data.entities.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity): Long

    @Update
    suspend fun updateNews(news: NewsEntity)

    @Delete
    suspend fun deleteNews(news: NewsEntity)

    @Query("SELECT * FROM news ORDER BY fechaPublicacion DESC")
    fun getNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getNewsById(id: Long): NewsEntity?
}