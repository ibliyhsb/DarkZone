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
import cl.duoc.app.model.data.entities.NewsEntity
import cl.duoc.app.model.data.dao.NewsDao

@Database(
    entities = [FormularioUsuarioEntity::class, FormularioBlogsEntity::class, NewsEntity::class],
    version = 8, // Incremented version to fix NewsEntity table name
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun formularioUsuarioDao(): FormularioUsuarioDao
    abstract fun formularioBlogsDao(): FormularioBlogsDao
    abstract fun newsDao(): NewsDao

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

        // Migration from version 4 to 5 - This migration was empty, perhaps a placeholder?
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // No changes were made in this migration.
            }
        }

        // Migration from version 5 to 6 - Remove foreign key constraint
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // ...existing code...
            }
        }

        // Migration from version 6 to 7 - Add NewsEntity table (incorrect name)
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS newsentity (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        titulo TEXT NOT NULL,
                        descripcion TEXT NOT NULL DEFAULT '',
                        contenido TEXT NOT NULL,
                        autor TEXT NOT NULL,
                        fechaPublicacion TEXT NOT NULL,
                        esPublicado INTEGER NOT NULL DEFAULT 0,
                        imagenUri TEXT
                    )
                """)
            }
        }

        // Migration from version 7 to 8 - Fix table name from newsentity to news
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop the incorrectly named table
                database.execSQL("DROP TABLE IF EXISTS newsentity")
                
                // Create the correctly named table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS news (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        titulo TEXT NOT NULL,
                        descripcion TEXT NOT NULL DEFAULT '',
                        contenido TEXT NOT NULL,
                        autor TEXT NOT NULL,
                        fechaPublicacion TEXT NOT NULL,
                        esPublicado INTEGER NOT NULL DEFAULT 0,
                        imagenUri TEXT
                    )
                """)
                
                // Clear old data from formulario_usuario to avoid ID conflicts with backend data
                database.execSQL("DELETE FROM formulario_usuario")
            }
        }
        
        // Migration from version 6 to 8 - Direct migration skipping version 7
        private val MIGRATION_6_8 = object : Migration(6, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the news table with correct name
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS news (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        titulo TEXT NOT NULL,
                        descripcion TEXT NOT NULL DEFAULT '',
                        contenido TEXT NOT NULL,
                        autor TEXT NOT NULL,
                        fechaPublicacion TEXT NOT NULL,
                        esPublicado INTEGER NOT NULL DEFAULT 0,
                        imagenUri TEXT
                    )
                """)
                
                // Clear old data from formulario_usuario to avoid ID conflicts with backend data
                database.execSQL("DELETE FROM formulario_usuario")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_6_8)
                    .fallbackToDestructiveMigration() // For development: recreate DB if migration fails
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}