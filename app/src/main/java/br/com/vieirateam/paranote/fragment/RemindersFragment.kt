package br.com.vieirateam.paranote.fragment

import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.actionmode.ActionModeNote
import br.com.vieirateam.paranote.entity.Note

class RemindersFragment : BaseNoteFragment() {

    override fun onActivityCreated() {
        mActionModeNote = ActionModeNote(this, fragmentTAG)
    }

    override fun getItems() {
        NoteApplication.noteViewModel.selectRemindersNotes()
        NoteApplication.noteViewModel.notes.observe(viewLifecycleOwner, {
            it?.let { notes ->
                adapter.setItems(notes)
                if (notes.isEmpty()) {
                    showMessage(getString(R.string.text_view_empty_notes_reminders), R.drawable.ic_drawable_reminder)
                } else {
                    hideMessage()
                }
            }
        })
    }

    override fun onBindSearch(newText: String?) {
        val filterNotes = mutableListOf<Note>()
        NoteApplication.noteViewModel.filterRemindersNotes(newText)
        NoteApplication.noteViewModel.notes.observe(viewLifecycleOwner, {
            it?.let { notes ->
                for (note in notes) {
                    if (note.reminder.isChecked) {
                        filterNotes.add(note)
                    }
                }
                adapter.setItems(filterNotes)
            }
        })
    }
}