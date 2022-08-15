package br.com.vieirateam.paranote.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.paranote.database.dao.NoteDAO
import br.com.vieirateam.paranote.entity.Note

class NoteRepository(private val noteDAO: NoteDAO) {

    lateinit var notes: LiveData<List<Note>>

    fun selectNotes() {
        notes = noteDAO.selectNotes()
    }

    fun selectArchivedNotes() {
        notes = noteDAO.selectArchivedNotes()
    }

    fun selectRemindersNotes() {
        notes = noteDAO.selectRemindersNotes()
    }

    fun selectFavoriteNotes() {
        notes = noteDAO.selectFavoriteNotes()
    }

    fun filterNotes(query: String?) {
        notes = noteDAO.filterNotes(query)
    }

    fun filterArchivedNotes(query: String?) {
        notes = noteDAO.filterArchivedNotes(query)
    }

    fun filterFavoriteNotes(query: String?) {
        notes = noteDAO.filterFavoriteNotes(query)
    }

    fun filterRemindersNotes(query: String?) {
        notes = noteDAO.filterRemindersNotes(query)
    }

    @WorkerThread
    suspend fun selectLastInsert(): Note?  = noteDAO.selectLastInsert()

    @WorkerThread
    suspend fun insert(item: Note) {
        noteDAO.insert(item)
    }

    @WorkerThread
    suspend fun update(item: Note) {
        noteDAO.update(item)
    }

    @WorkerThread
    suspend fun delete(item: Note) {
        noteDAO.delete(item)
    }
}