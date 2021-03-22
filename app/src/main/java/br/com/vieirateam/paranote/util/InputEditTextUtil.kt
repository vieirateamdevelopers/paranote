package br.com.vieirateam.paranote.util

import hey.jusang.undoedittext.UndoEditText

class InputEditTextUtil(private val undoEditText: UndoEditText) {

    init {
        undoEditText.setMaxHistorySize(UndoEditText.HISTORY_INFINITE)
    }

    fun undo() {
        if(undoEditText.canUndo()) {
            undoEditText.undo()
        }
    }

    fun redo() {
        if(undoEditText.canRedo()) {
            undoEditText.redo()
        }
    }

    fun clear() {
        undoEditText.clearHistory()
    }

    fun getText(): String = undoEditText.text.toString().trim()

    fun requestFocus() { undoEditText.requestFocus() }

    fun setText(value: String?) { undoEditText.setText(value) }

    fun setHint(value: String) { undoEditText.hint = value }

    fun setSelection(position: Int) { undoEditText.setSelection(position) }

    fun show() { KeyboardUtil.show(undoEditText) }

    fun hide() { KeyboardUtil.hide(undoEditText) }
}