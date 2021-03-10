package br.com.vieirateam.paranote.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

class TextAnalyzerUtil {

    private val recognizer = TextRecognition.getClient()
    var resultText: String? = null

    fun getText(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(inputImage).addOnSuccessListener { result ->
            resultText = result.text
            Log.d(ConstantsUtil.TAG, resultText.toString())
        }
        .addOnFailureListener { exception ->
            resultText = null
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    fun getText(context: Context, uri: Uri) {
        val inputImage = InputImage.fromFilePath(context, uri)
        recognizer.process(inputImage).addOnSuccessListener { result ->
            resultText = result.text
            Log.d(ConstantsUtil.TAG, resultText.toString())
        }
        .addOnFailureListener { exception ->
            resultText = null
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }
}