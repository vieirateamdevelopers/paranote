package br.com.vieirateam.paranote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.vieirateam.paranote.database.NoteDatabase
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application), ViewModelScope<Note> {

    private var noteDAO = NoteDatabase.getDatabase().noteDAO()
    private var noteRepository = NoteRepository(noteDAO)
    lateinit var notes: LiveData<List<Note>>

    override fun getScope(): CoroutineScope = viewModelScope

    suspend fun selectLastInsert() = noteRepository.selectLastInsert()

    override fun insert(item: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.insert(item)
    }

    override fun update(item: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.update(item)
    }

    override fun delete(item: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.delete(item)
    }

    fun selectNotes() {
        noteRepository.selectNotes()
        notes = noteRepository.notes
    }

    fun selectArchivedNotes() {
        noteRepository.selectArchivedNotes()
        notes = noteRepository.notes
    }

    fun selectRemindersNotes() {
        noteRepository.selectRemindersNotes()
        notes = noteRepository.notes
    }

    fun selectFavoriteNotes() {
        noteRepository.selectFavoriteNotes()
        notes = noteRepository.notes
    }

    fun filterNotes(query: String?) {
        noteRepository.filterNotes("%$query%")
        notes = noteRepository.notes
    }

    fun filterArchivedNotes(query: String?) {
        noteRepository.filterArchivedNotes("%$query%")
        notes = noteRepository.notes
    }

    fun filterFavoriteNotes(query: String?) {
        noteRepository.filterFavoriteNotes("%$query%")
        notes = noteRepository.notes
    }

    fun filterRemindersNotes(query: String?) {
        noteRepository.filterRemindersNotes("%$query%")
        notes = noteRepository.notes
    }
}