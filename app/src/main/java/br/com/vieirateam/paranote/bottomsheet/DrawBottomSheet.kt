package br.com.vieirateam.paranote.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.DrawUtil
import br.com.vieirateam.paranote.util.TextAnalyzerUtil
import kotlinx.android.synthetic.main.bottom_sheet_draw.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_clear
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_redo
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_undo

class DrawBottomSheet(private val context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_draw) {

    private lateinit var mDrawUtil: DrawUtil
    private lateinit var mTextAnalyzerUtil: TextAnalyzerUtil

    override fun build() {
        mDrawUtil = mView.findViewById(R.id.draw_view)
        mDrawUtil.setDrawBottomSheet(this)
        mView.bottom_sheet_draw.minimumHeight = mBottomSheetHeight
        mView.image_view_base_send.setImageResource(R.drawable.ic_drawable_save)
        mCircularProgressIndicator = mView.progress_circular_draw
        mTextAnalyzerUtil = TextAnalyzerUtil()

        mView.image_view_base_send.setOnClickListener {
            val bitmap = mDrawUtil.getBitmap()
            mTextAnalyzerUtil.getText(context, bitmap, this)
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

    override fun finishTextRecognition() {
        mBottomSheetText = mTextAnalyzerUtil.resultText
        listener.setOnBottomSheetClickListener("draw")
        hide()
    }
}