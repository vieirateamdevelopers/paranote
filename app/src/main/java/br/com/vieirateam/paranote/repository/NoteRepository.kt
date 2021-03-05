package br.com.vieirateam.paranote.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.com.vieirateam.paranote.database.dao.NoteDAO
import br.com.vieirateam.paranote.entity.Note

class NoteRepository(private val noteDAO: NoteDAO) {

    lateinit var notes: LiveData<List<Note>>

    fun selectAllNotes() {
        notes = noteDAO.selectAllNotes()
    }

    fun selectNotes() {
        notes = noteDAO.selectNotes()
    }

    fun selectArchivedNotes() {
        notes = noteDAO.selectArchivedNotes()
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