package br.com.vieirateam.paranote.actionmode

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.fragment.BaseNoteFragment
import br.com.vieirateam.paranote.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_color_dark.view.*
import kotlinx.android.synthetic.main.bottom_sheet_color_light.view.*
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class ActionModeNote(private val fragment: BaseNoteFragment, fragmentTAG: String):
    ActionModeBase<Note>(fragment.activity, fragmentTAG), BottomSheet.Callback {

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
        val notes = mutableListOf<Note>()
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
                    notes.add(item)
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
                    notes.add(item)
                }
                "color" -> {
                    notes.add(item)
                }
            }
        }
        when(option) {
            "delete" -> configureBottomSheetDelete(notes)
            "color" -> configureBottomSheetColor(notes)
            "duplicate" -> duplicateNotes(notes)
        }
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

    private fun duplicateNotes(notes: List<Note>) {
        NoteApplication.getViewModel()
        NoteApplication.noteViewModel.getScope().launch(Dispatchers.Main) {
            for (item in notes) {
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

    private fun configureBottomSheetDelete(notes: List<Note>) {
        val context = this.context as AppCompatActivity
        val builder = BottomSheet.Builder(context, R.layout.bottom_sheet_confirm, this)
        builder.build()
        val dialogView = builder.getBottomSheetView()
        dialogView.text_view_confirm_title.text = context.getString(R.string.menu_delete)
        dialogView.text_view_confirm_body.text = context.getString(R.string.text_view_delete)
        dialogView.image_view_confirm.setImageResource(R.drawable.ic_drawable_delete)

        dialogView.button_cancel.setOnClickListener {
            builder.setHide()
        }

        dialogView.button_confirm.setOnClickListener {
            for (note in notes) {
                NotificationUtil.cancel(note)
                NoteApplication.noteViewModel.delete(note)
            }
            builder.setHide()
        }
    }

    private fun configureBottomSheetColor(notes: List<Note>) {
        val context = this.context as AppCompatActivity
        val layoutID = if (UserPreferenceUtil.darkMode) {
            R.layout.bottom_sheet_color_dark
        } else {
            R.layout.bottom_sheet_color_light
        }
        val builder = BottomSheet.Builder(context, layoutID, this).apply {
            setTitle(context.getString(R.string.text_view_color))
            setInflateMenus()
            build()
        }
        val dialogView = builder.getBottomSheetView()
        if (UserPreferenceUtil.darkMode) {
            dialogView.bottom_sheet_color_dark.minimumHeight = builder.getBottomSheetHeight()
        } else {
            dialogView.bottom_sheet_color_light.minimumHeight = builder.getBottomSheetHeight()
        }
        val colors = ColorsUtil.getColors(dialogView)
        for (color in colors) {
            val view = color.circleImageView
            view?.setOnClickListener {
                for (note in notes) {
                    note.color = color.id
                    NoteApplication.noteViewModel.update(note)
                }
                builder.setHide()
            }
        }
    }

    override fun onBackPressed() {}

    override fun onClickListener(view: View) {}

    override fun onStartTextRecognition(dialog: BottomSheetDialog) {}

    override fun onFinishTextRecognition() {}
}