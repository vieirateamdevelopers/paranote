package br.com.vieirateam.paranote.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object NoteMigrate {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}