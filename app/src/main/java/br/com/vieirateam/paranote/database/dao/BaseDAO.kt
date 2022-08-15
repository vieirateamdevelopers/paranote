package br.com.vieirateam.paranote.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface BaseDAO<T> {

    @Insert(onConflict = REPLACE)
    suspend fun insert(item : T)

    @Update
    suspend fun update(item : T)

    @Delete
    suspend fun delete(item : T)
}