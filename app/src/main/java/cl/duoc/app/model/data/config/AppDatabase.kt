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
    version = 6, // Incremented version from 5 to 6
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

        // Migration from version 4 to 5 - This migration was empty, perhaps a placeholder?
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // No changes were made in this migration.
            }
        }

        // Migration from version 5 to 6 - Remove foreign key constraint
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table without the foreign key constraint
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS formulario_blog_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        titulo TEXT NOT NULL,
                        descripcion TEXT NOT NULL DEFAULT '',
                        contenido TEXT NOT NULL,
                        usuario_autor TEXT NOT NULL,
                        fechaPublicacion TEXT NOT NULL,
                        esPublicado INTEGER NOT NULL DEFAULT 0,
                        imagenUri TEXT
                    )
                """.trimIndent())
                
                // Copy data from old table to new table
                database.execSQL("""
                    INSERT INTO formulario_blog_new (id, titulo, descripcion, contenido, usuario_autor, fechaPublicacion, esPublicado, imagenUri)
                    SELECT id, titulo, descripcion, contenido, usuario_autor, fechaPublicacion, esPublicado, imagenUri
                    FROM formulario_blog
                """.trimIndent())
                
                // Drop the old table
                database.execSQL("DROP TABLE formulario_blog")
                
                // Rename the new table to the old table name
                database.execSQL("ALTER TABLE formulario_blog_new RENAME TO formulario_blog")
                
                // Recreate the index
                database.execSQL("CREATE INDEX IF NOT EXISTS index_formulario_blog_usuario_autor ON formulario_blog(usuario_autor)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6) // Add all migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}