package br.com.vieirateam.paranote.util

import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.fragment.BaseNoteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

class TextAnalyzerUtil(private val context: AppCompatActivity) {

    private val recognizer = TextRecognition.getClient()
    var resultText: String? = null

    fun getText(fragment: BaseNoteFragment, bitmap: Bitmap, dialog: BottomSheetDialog) {
        val progress: CircularProgressIndicator = if (fragment.showBottom == "draw") {
            fragment.mDialogView.findViewById(R.id.progress_circular_draw)
        } else {
            fragment.mDialogView.findViewById(R.id.progress_circular_image)
        }
        progress.show()
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val processImage = recognizer.process(inputImage)

        processImage.addOnSuccessListener { result ->
            resultText = result.text
            if (resultText.toString().trim().isNotEmpty() && resultText!= null) {
                ToastUtil.show(context, context.getString(R.string.text_view_text_recognition_success))
                dialog.hide()
            }
            Log.d(ConstantsUtil.TAG, resultText.toString())
            return@addOnSuccessListener
        }
        processImage.addOnFailureListener { exception ->
            resultText = null
            ToastUtil.show(context, context.getString(R.string.text_view_text_recognition_failure))
            dialog.hide()
            Log.d(ConstantsUtil.TAG, exception.toString())
            return@addOnFailureListener
        }
        processImage.addOnCompleteListener {
            progress.hide()
            fragment.onFinishTextRecognition()
            return@addOnCompleteListener
        }
    }
}