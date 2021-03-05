package br.com.vieirateam.paranote.bottomsheet

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.util.ColorsUtil
import br.com.vieirateam.paranote.util.ConstantsUtil
import kotlinx.android.synthetic.main.bottom_sheet_base.view.bottom_sheet_base
import kotlinx.android.synthetic.main.bottom_sheet_base.view.text_input_base_body
import kotlinx.android.synthetic.main.bottom_sheet_base.view.linear_layout_base_buttons
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_clear
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send

class NoteBottomSheet(private val context: AppCompatActivity, listener: Callback, private val note: Note?):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_base) {

    override fun build() {
        mView.bottom_sheet_base.minimumHeight = mBottomSheetHeight
        if (note == null) {
            mView.linear_layout_base_buttons.visibility = View.INVISIBLE
        } else {
            mView.linear_layout_base_buttons.visibility = View.VISIBLE
            ColorsUtil.setBackgroundColor(note, mView)
        }
        setOnScrollChangeListener()
        mView.text_input_base_body.minHeight = context.window.decorView.height

        mView.image_view_base_clear.setOnClickListener {
            lockBottomSheet(true)
            listener.setOnBottomSheetClickListener("clear")
        }

        mView.image_view_base_send.setImageResource(R.drawable.ic_drawable_send)
        mView.image_view_base_send.setOnClickListener {
            listener.setOnBottomSheetClickListener("send")
            hide()
        }
    }

    private fun setOnScrollChangeListener() {
        try {
            mView.text_input_base_body.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY) {
                    lockBottomSheet(true)
                    mView.linear_layout_base_buttons.visibility = View.INVISIBLE
                } else {
                    lockBottomSheet(false)
                    mView.linear_layout_base_buttons.visibility = View.VISIBLE
                }
            }
        } catch (exception: NullPointerException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }
}