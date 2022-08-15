package br.com.vieirateam.paranote.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import br.com.vieirateam.paranote.entity.Note

@Dao
interface NoteDAO: BaseDAO<Note> {

    @Query("SELECT * FROM note ORDER BY note_id DESC LIMIT 1")
    suspend fun selectLastInsert(): Note?

    @Query("SELECT * FROM note WHERE note_archived == 0 AND note_favorite == 0 AND note_reminder_checked == 0 ORDER BY note_id DESC")
    fun selectNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_archived == 0 AND note_favorite == 1 AND note_reminder_checked == 0 ORDER BY note_id DESC")
    fun selectFavoriteNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_archived == 0 AND note_reminder_checked == 1 ORDER BY note_id DESC")
    fun selectRemindersNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_archived == 1 ORDER BY note_id DESC")
    fun selectArchivedNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_archived == 0 AND note_favorite == 0 AND note_title LIKE :query OR note_body LIKE :query ORDER BY note_id DESC")
    fun filterNotes(query: String?) : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_archived == 0 AND note_favorite == 1 AND note_title LIKE :query OR note_body LIKE :query ORDER BY note_id DESC")
    fun filterFavoriteNotes(query: String?) : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_archived == 0 AND note_reminder_checked == 1 AND note_title LIKE :query OR note_body LIKE :query ORDER BY note_id DESC")
    fun filterRemindersNotes(query: String?) : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_archived == 1 AND note_title LIKE :query OR note_body LIKE :query ORDER BY note_id DESC")
    fun filterArchivedNotes(query: String?) : LiveData<List<Note>>
}