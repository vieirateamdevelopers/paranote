package br.com.vieirateam.paranote.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.util.Log
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note

object OptionsUtil {

    fun copy(context: Context, message: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(context.getString(R.string.menu_copy), message)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun share(context: Context, message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.menu_share)))
        } catch (exception: ActivityNotFoundException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    fun getMessage(note: Note) : String {
        return if (note.title.isEmpty()) {
            note.body
        } else {
            "${note.title}\n${note.body}"
        }
    }
}