package br.com.vieirateam.paranote.fragment

import androidx.lifecycle.Observer
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.actionmode.ActionModeNote
import br.com.vieirateam.paranote.entity.Note

class NoteFragment : BaseNoteFragment() {

    override fun onActivityCreated() {
        mActionModeNote = ActionModeNote(this, false)
    }

    override fun getItems() {
        NoteApplication.noteViewModel.selectNotes()
        NoteApplication.noteViewModel.notes.observe(viewLifecycleOwner, Observer {
            it?.let { notes ->
                adapter.setItems(notes)
                if (notes.isEmpty()) {
                    showMessage(getString(R.string.text_view_empty_notes), R.drawable.ic_drawable_note)
                } else {
                    hideMessage()
                }
            }
        })
    }

    override fun onBindSearch(newText: String?) {
        val listNotes = mutableListOf<Note>()
        NoteApplication.noteViewModel.filterNotes(newText)
        NoteApplication.noteViewModel.notes.observe(viewLifecycleOwner, Observer {
            it?.let { notes ->
                for (note in notes) {
                    if (!note.favorite && !note.archived) {
                        listNotes.add(note)
                    }
                }
                adapter.setItems(listNotes)
            }
        })
    }
}