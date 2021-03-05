package br.com.vieirateam.paranote.bottomsheet

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.util.ColorsUtil
import kotlinx.android.synthetic.main.bottom_sheet_color.view.bottom_sheet_color

class ColorBottomSheet(context: AppCompatActivity, listener: Callback, private val note: Note, private val mViewTemp: View):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_color) {

    override fun build() {
        mView.bottom_sheet_color.minimumHeight = mBottomSheetHeight

        for (pair in ColorsUtil.getColors(mView)) {
            val color = pair.first
            val view = pair.second
            ColorsUtil.setCircleBackgroundColor(note, color, view)

            view.setOnClickListener {
                mBottomSheetColor = color
                listener.setOnBottomSheetClickListener("color")
                ColorsUtil.setBackgroundColor(note, mViewTemp)
                hide()
            }
        }
    }
}