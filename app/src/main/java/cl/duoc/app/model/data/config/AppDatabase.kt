package cl.duoc.app.model.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.app.model.data.dao.FormularioBlogsDao
import cl.duoc.app.model.data.dao.FormularioComentariosDao
import cl.duoc.app.model.data.dao.FormularioUsuarioDao
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.entities.FormularioComentariosEntity
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity

@Database(
    entities = [FormularioUsuarioEntity::class, FormularioBlogsEntity::class, FormularioComentariosEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun formularioUsuarioDao(): FormularioUsuarioDao
    abstract fun formularioBlogsDao(): FormularioBlogsDao
    abstract fun formularioComentariosDao(): FormularioComentariosDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}