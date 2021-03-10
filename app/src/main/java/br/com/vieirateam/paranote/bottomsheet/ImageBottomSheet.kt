package br.com.vieirateam.paranote.bottomsheet

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.TextAnalyzerUtil
import kotlinx.android.synthetic.main.bottom_sheet_image.view.bottom_sheet_image
import kotlinx.android.synthetic.main.bottom_sheet_image.view.image_view_image
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_clear
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send

class ImageBottomSheet(private val imageUri: Uri, private val context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_image) {

    override fun build() {
        mView.bottom_sheet_image.minimumHeight = mBottomSheetHeight
//        mView.image_view_base_clear.setImageResource(R.drawable.ic_drawable_crop_rotate)
        mView.image_view_image.setImageURI(imageUri)
        val textAnalyzerUtil = TextAnalyzerUtil()
        textAnalyzerUtil.getText(context, imageUri)

        mView.image_view_base_send.setOnClickListener {
            mBottomSheetText = textAnalyzerUtil.resultText
            mBottomSheetListener.setOnBottomSheetClickListener("send_image")
            hide()
        }

//        mView.image_view_base_clear.setOnClickListener {
//
//        }
    }
}