package br.com.vieirateam.paranote.util

import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatUtil {

    fun format(date: Date, isDate: Boolean, locale: Locale = Locale.getDefault()): String {
        val context = NoteApplication.getInstance().applicationContext
        val format = if (isDate) {
            context.getString(R.string.text_view_format_date)
        } else {
            context.getString(R.string.text_view_format_hour)
        }
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(date)
    }
}