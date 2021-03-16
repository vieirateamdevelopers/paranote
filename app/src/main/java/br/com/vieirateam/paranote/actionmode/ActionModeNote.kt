package br.com.vieirateam.paranote.actionmode

import android.view.View
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.fragment.BaseNoteFragment
import br.com.vieirateam.paranote.util.ColorsUtil
import br.com.vieirateam.paranote.util.NotificationUtil
import br.com.vieirateam.paranote.util.OptionsUtil
import br.com.vieirateam.paranote.util.UserPreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class ActionModeNote(private val fragment: BaseNoteFragment, archive: Boolean):
    ActionModeBase<Note>(fragment.activity, archive) {

    private val items = fragment.adapter.baseItemsSelected

    override fun addSelectedItem(item: Note) {
        items.add(item)
    }

    override fun removeSelectedItem(item: Note) {
        items.remove(item)
    }

    override fun clearSelectedItems() {
        items.clear()
    }

    override fun actionModeClicked(option: String) {
        val stringBuilder = StringBuilder()
        val lastPosition = items.size - 1
        for ((position, item) in items.withIndex()) {
            when(option) {
                "archive" -> {
                    item.archived = !item.archived
                    NoteApplication.noteViewModel.update(item)
                } "favorite" -> {
                    item.favorite = !item.favorite
                    NoteApplication.noteViewModel.update(item)
                } "delete" -> {
                    NotificationUtil.cancel(item)
                    NoteApplication.noteViewModel.delete(item)
                } "copy" -> {
                    if (isLastPosition(stringBuilder, item, position, lastPosition)) {
                        OptionsUtil.copy(context, stringBuilder.toString())
                        break
                    }
                } "share" -> {
                    if (isLastPosition(stringBuilder, item, position, lastPosition)) {
                        OptionsUtil.share(context, stringBuilder.toString())
                        break
                    }
                } "duplicate" -> {
                    duplicateNote(item)
                }
            }
        }
        stringBuilder.clear()
    }

    override fun notifyDataSetChanged() {
        fragment.adapter.notifyDataSetChanged()
    }

    override fun setCardBackgroundColor(item: Note, view: View, selected: Boolean) {
        ColorsUtil.setCardBackgroundColor(item, view, selected)
    }

    override fun hideFloatingButton(hide: Boolean) {
        fragment.hideFloatingButton(hide)
    }

    private fun isLastPosition(stringBuilder: StringBuilder, item: Note, position: Int, lastPosition: Int): Boolean {
        stringBuilder.append(OptionsUtil.getMessage(item))
        return if (position < lastPosition) {
            stringBuilder.append("\n\n")
            false
        } else {
            true
        }
    }

    private fun duplicateNote(item: Note) {
        NoteApplication.getViewModel()
        NoteApplication.noteViewModel.getScope().launch(Dispatchers.Main) {
            val result = NoteApplication.noteViewModel.selectLastInsert()
            if (result == null) {
                UserPreferenceUtil.noteID = 1
            } else {
                UserPreferenceUtil.noteID = result.id
                UserPreferenceUtil.noteID = UserPreferenceUtil.noteID + 1
            }
            val note = Note()
            note.id = UserPreferenceUtil.noteID
            note.title = item.title
            note.body = item.body
            note.color = item.color
            note.favorite = item.favorite
            note.archived = item.archived
            note.reminder = item.reminder
            NoteApplication.noteViewModel.insert(note)
        }
    }
}