package br.com.vieirateam.paranote.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import hey.jusang.undoedittext.UndoEditText

object KeyboardUtil {

    fun show(view: UndoEditText) {
        view.requestFocus()
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        view.requestFocus()
    }

    fun hide(view: View) {
        view.requestFocus()
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}