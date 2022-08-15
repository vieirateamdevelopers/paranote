package br.com.vieirateam.paranote.database

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.database.dao.NoteDAO
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.ConvertersUtil

@TypeConverters(ConvertersUtil::class)

@Database(entities = [Note::class], version = 1, exportSchema = false)

abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDAO(): NoteDAO

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val context = NoteApplication.getInstance().applicationContext
                val instance = Room.databaseBuilder(context, NoteDatabase::class.java, ConstantsUtil.DATABASE_NAME)
                    .addMigrations(NoteMigrate.MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}