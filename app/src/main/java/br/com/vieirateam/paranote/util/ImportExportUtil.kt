package br.com.vieirateam.paranote.util

import android.content.Context
import android.graphics.Bitmap
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.lang.StringBuilder

object ImportExportUtil {

    fun getTextFromImageFile(bitmap: Bitmap, context: Context): String {
        val mStringBuilder = StringBuilder()
        val mTextRecognizer = TextRecognizer.Builder(context).build()
        val mFrame = Frame.Builder().setBitmap(bitmap).build()
        val textBlocks: SparseArray<TextBlock> = mTextRecognizer.detect(mFrame)

        for (i in 0 until textBlocks.size()) {
            val item = textBlocks.get(textBlocks.keyAt(i))
            mStringBuilder.append(item.value)
            mStringBuilder.append("\n")
        }
        return mStringBuilder.toString()
    }
}