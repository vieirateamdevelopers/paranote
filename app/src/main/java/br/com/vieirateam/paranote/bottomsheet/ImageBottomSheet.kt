package br.com.vieirateam.paranote.bottomsheet

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.TextAnalyzerUtil
import kotlinx.android.synthetic.main.bottom_sheet_image.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_clear
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send

class ImageBottomSheet(private val imageUri: Uri, private val context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_image) {

    private lateinit var mTextAnalyzerUtil: TextAnalyzerUtil

    override fun build() {
        mView.bottom_sheet_image.minimumHeight = mBottomSheetHeight
        mView.image_view_base_clear.setImageResource(R.drawable.ic_drawable_crop_rotate)
        mView.image_view_image.setImageURI(imageUri)
        mCircularProgressIndicator = mView.progress_circular_image
        mTextAnalyzerUtil = TextAnalyzerUtil()
        mView.image_view_base_send.setOnClickListener {
            val bitmap = getBitmap()
            mTextAnalyzerUtil.getText(context, bitmap, this)
        }

        mView.image_view_base_clear.setOnClickListener {
            mView.progress_circular_image.show()
            val bitmap = rotate(getBitmap())
            Handler().postDelayed({
                mView.image_view_image.setImageBitmap(bitmap)
                mView.progress_circular_image.hide()
            }, ConstantsUtil.ROTATE_DELAY)
        }
    }

    override fun finishTextRecognition() {
        mBottomSheetText = mTextAnalyzerUtil.resultText
        mBottomSheetListener.setOnBottomSheetClickListener("send_image")
        hide()
    }

    private fun rotate(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { postRotate(90F) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getBitmap(): Bitmap {
        val bitmapDrawable = mView.image_view_image.drawable as BitmapDrawable
        return bitmapDrawable.bitmap
    }
}