package br.com.vieirateam.paranote.adapter

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.util.ColorsUtil
import br.com.vieirateam.paranote.util.DateFormatUtil
import kotlinx.android.synthetic.main.adapter_card_view.view.*

class NoteAdapter (
    val onItemClick: (Note, View) -> Unit,
    val onItemLongClick: (Note, View) -> Unit,
    val onReminderItemClick: (Note) -> Unit):
    BaseAdapter<Note>(R.layout.adapter_card_view) {

    override fun onItemsView(item: Note, view: View) {
        if (baseItemsSelected.contains(item)) {
            setCardBackgroundColor(item, view, true)
            view.floating_button_check.show()
        } else {
            setCardBackgroundColor(item, view, false)
            view.floating_button_check.hide()
        }

        if (item.title.isEmpty()) {
            view.text_view.text = item.body
        } else {
            val text = SpannableString("${item.title}\n\n${item.body}")
            text.setSpan(RelativeSizeSpan(1.2f), 0, item.title.length, 0)
            text.setSpan(StyleSpan(Typeface.BOLD), 0, item.title.length, 0)
            view.text_view.text = text
        }

        if (item.reminder.isChecked) {
            setLayoutParams(view.text_view, 32, 32, 32, 0)
            setLayoutParams(view.floating_button_mini, 16, 16, 16, 16)
            view.floating_button_mini.text = getDateTime(item)
            view.floating_button_mini.visibility = View.VISIBLE

            view.floating_button_mini.setOnClickListener {
                onReminderItemClick(item)
            }
        } else {
            setLayoutParams(view.text_view, 32, 32, 32, 0)
            setLayoutParams(view.floating_button_mini, 0, 0, 0, 0)
            view.floating_button_mini.visibility = View.INVISIBLE
        }
    }

    override fun onClick(item: Note, view: View) {
        onItemClick(item, view)
    }

    override fun onLongClick(item: Note, view: View) {
        onItemLongClick(item, view)
    }

    override fun setItems(baseItems: List<Note>) {
        this.genericItems = baseItems
        notifyDataSetChanged()
    }

    override fun getItems(): List<Note> = this.genericItems

    private fun setLayoutParams(view: View, marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int) {
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        view.layoutParams = layoutParams
    }

    private fun setCardBackgroundColor(item: Note, view: View, selected: Boolean) {
        ColorsUtil.setCardBackgroundColor(item, view, selected)
    }

    private fun getDateTime(item: Note) : String {
        val date = DateFormatUtil.format(item.reminder.dateTime.time, true)
        val hour = DateFormatUtil.format(item.reminder.dateTime.time, false)
        return "$date $hour"
    }
}