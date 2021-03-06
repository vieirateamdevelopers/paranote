package br.com.vieirateam.paranote.util

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

object TextAnalyzerUtil {

    fun getText(inputImage: InputImage) {
        val recognizer = TextRecognition.getClient()
        val result = recognizer.process(inputImage)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                Log.d(ConstantsUtil.TAG, exception.toString())
            }
    }
}