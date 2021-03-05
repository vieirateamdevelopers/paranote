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
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view
import kotlinx.android.synthetic.main.adapter_card_view.view.floating_button_mini
import kotlinx.android.synthetic.main.adapter_card_view.view.floating_button_check

class NoteAdapter (val onItemClick: (Note, View) -> Unit,
                   val onItemLongClick: (Note, View) -> Unit,
                   val onReminderItemClick: (Note) -> Unit):
    BaseAdapter<Note>(R.layout.adapter_card_view) {

    override fun onItemsView(item: Note, view: View) {
        if (baseItemsSelected.contains(item)) {
            view.floating_button_check.show()
            setCardBackgroundColor(item, view, true)
        } else {
            view.floating_button_check.hide()
            setCardBackgroundColor(item, view, false)
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
            setLayoutParams(view, 16)
            view.floating_button_mini.text = getDateTime(item)
            view.floating_button_mini.visibility = View.VISIBLE

            view.floating_button_mini.setOnClickListener {
                onReminderItemClick(item)
            }
        } else {
            setLayoutParams(view, 0)
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

    private fun setLayoutParams(view: View, margin: Int) {
        val layoutParams = view.floating_button_mini.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(margin, margin, margin, margin)
        view.floating_button_mini.layoutParams = layoutParams
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