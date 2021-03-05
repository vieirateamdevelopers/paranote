package br.com.vieirateam.paranote.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.DrawUtil
import kotlinx.android.synthetic.main.bottom_sheet_draw.view.bottom_sheet_draw
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_clear
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_redo
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_undo

class DrawBottomSheet(context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_draw) {

    private lateinit var mDrawUtil: DrawUtil

    override fun build() {
        mDrawUtil = mView.findViewById(R.id.draw_view)
        mDrawUtil.setDrawBottomSheet(this)
        mView.bottom_sheet_draw.minimumHeight = mBottomSheetHeight

        mView.image_view_base_send.setImageResource(R.drawable.ic_drawable_save)
        mView.image_view_base_send.setOnClickListener {
            mBottomSheetText = mDrawUtil.getTextFromBitmap()
            listener.setOnBottomSheetClickListener("draw")
            hide()
        }

        mView.image_view_base_clear.setOnClickListener {
            mDrawUtil.clear()
        }

        mView.image_view_base_undo.setOnClickListener {
            mDrawUtil.undo()
        }

        mView.image_view_base_redo.setOnClickListener {
            mDrawUtil.redo()
        }
    }
}