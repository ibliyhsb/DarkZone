package cl.duoc.app.model.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.app.model.data.dao.FormularioBlogsDao
import cl.duoc.app.model.data.dao.FormularioUsuarioDao
import cl.duoc.app.model.data.entities.FormularioBlogsEntity
import cl.duoc.app.model.data.entities.FormularioUsuarioEntity

@Database(
    entities = [FormularioUsuarioEntity::class, FormularioBlogsEntity::class],
    version = 4, // Incremented version from 3 to 4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun formularioUsuarioDao(): FormularioUsuarioDao
    abstract fun formularioBlogsDao(): FormularioBlogsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 3 to 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new 'descripcion' column to the 'formulario_blog' table
                database.execSQL("ALTER TABLE formulario_blog ADD COLUMN descripcion TEXT NOT NULL DEFAULT ''")
                // Add the new 'imagenUri' column to the 'formulario_blog' table
                database.execSQL("ALTER TABLE formulario_blog ADD COLUMN imagenUri TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .addMigrations(MIGRATION_3_4) // Add the migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}