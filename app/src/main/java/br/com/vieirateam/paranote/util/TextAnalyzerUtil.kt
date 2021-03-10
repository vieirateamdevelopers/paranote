package br.com.vieirateam.paranote.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.bottomsheet.BaseBottomSheet
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

class TextAnalyzerUtil {

    private val recognizer = TextRecognition.getClient()
    var resultText: String? = null

    fun getText(context: Context, bitmap: Bitmap, bottomSheet: BaseBottomSheet) {
        bottomSheet.mCircularProgressIndicator.show()
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val processImage = recognizer.process(inputImage)

        processImage.addOnSuccessListener { result ->
            resultText = result.text
            if (resultText.toString().trim().isNotEmpty() && resultText!= null) {
                ToastUtil.show(context, context.getString(R.string.text_view_recognizer_sucess))
            }
            Log.d(ConstantsUtil.TAG, resultText.toString())
            return@addOnSuccessListener
        }
        processImage.addOnFailureListener { exception ->
            resultText = null
            ToastUtil.show(context, context.getString(R.string.text_view_recognizer_failure))
            Log.d(ConstantsUtil.TAG, exception.toString())
            return@addOnFailureListener
        }
        processImage.addOnCompleteListener {
            bottomSheet.mCircularProgressIndicator.hide()
            bottomSheet.finishTextRecognition()
            return@addOnCompleteListener
        }
    }
}