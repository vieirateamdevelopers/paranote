package br.com.vieirateam.paranote.fragment

import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.actionmode.ActionModeNote
import br.com.vieirateam.paranote.entity.Note

class NotesFragment : BaseNoteFragment() {

    override fun onActivityCreated() {
        mActionModeNote = ActionModeNote(this, fragmentTAG)
    }

    override fun getItems() {
        NoteApplication.noteViewModel.selectNotes()
        NoteApplication.noteViewModel.notes.observe(viewLifecycleOwner, {
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
        val filterNotes = mutableListOf<Note>()
        NoteApplication.noteViewModel.filterNotes(newText)
        NoteApplication.noteViewModel.notes.observe(viewLifecycleOwner, {
            it?.let { notes ->
                for (note in notes) {
                    if (!note.favorite && !note.archived && !note.reminder.isChecked) {
                        filterNotes.add(note)
                    }
                }
                adapter.setItems(filterNotes)
            }
        })
    }
}