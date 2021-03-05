package br.com.vieirateam.paranote.bottomsheet

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.ImportExportUtil
import kotlinx.android.synthetic.main.bottom_sheet_image.view.bottom_sheet_image
import kotlinx.android.synthetic.main.bottom_sheet_image.view.image_view_image
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_clear
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send
import java.lang.NullPointerException

class ImageBottomSheet(private val path: Uri, private val context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_image) {

    private var rotation: Float = 0f
    private lateinit var mBitmap: Bitmap

    override fun build() {
        mView.bottom_sheet_image.minimumHeight = mBottomSheetHeight
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, path)
            setImageBitmap()

            mView.image_view_base_send.setOnClickListener {
                mBottomSheetText = ImportExportUtil.getTextFromImageFile(getBitmap(mBitmap), context)
                mBottomSheetListener.setOnBottomSheetClickListener("send_image")
                hide()
            }

            mView.image_view_base_clear.setImageResource(R.drawable.ic_drawable_crop_rotate)
            mView.image_view_base_clear.setOnClickListener {
                rotation += 90f
                mBitmap = getBitmap(mBitmap)
                setImageBitmap()
                if (rotation == 360f) {
                    rotation = 0f
                }
            }
        } catch (exception: NullPointerException) {
            val error = exception.toString()
            Log.d(ConstantsUtil.TAG, error)
            mBottomSheetDialog.dismiss()
            Toast.makeText(context, context.getString(R.string.text_view_activity_error, error), Toast.LENGTH_LONG).show()
        }
    }

    private fun setImageBitmap() {
        mView.image_view_image.setImageDrawable(BitmapDrawable(context.resources, mBitmap))
    }

    private fun getBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(rotation)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}