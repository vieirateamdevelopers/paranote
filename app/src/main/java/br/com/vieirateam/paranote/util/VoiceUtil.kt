package br.com.vieirateam.paranote.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.*
import java.util.Locale

class VoiceUtil(private val context: AppCompatActivity, private val view: View): RecognitionListener {

    private var recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var mSpeechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    init {
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mSpeechRecognizer.setRecognitionListener(this)
        mSpeechRecognizer.startListening(recognizerIntent)
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(ConstantsUtil.TAG, "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(ConstantsUtil.TAG, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(ConstantsUtil.TAG, "onRmsChanged")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(ConstantsUtil.TAG, "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        Log.d(ConstantsUtil.TAG, "onEndOfSpeech")
    }

    override fun onError(error: Int) {
        Log.d(ConstantsUtil.TAG, "onError: $error")
        view.image_view_confirm.setImageResource(R.drawable.ic_drawable_voice_red)
        view.text_view_confirm_body.hint = context.getText(R.string.text_view_voice_recognition_failure)

        view.image_view_confirm.setOnClickListener {
            view.text_view_confirm_body.text = ""
            view.text_view_confirm_body.hint = context.getText(R.string.text_view_voice_recognition_body)
            view.image_view_confirm.setImageResource(R.drawable.ic_drawable_voice_white)
            mSpeechRecognizer.startListening(recognizerIntent)
        }
    }

    fun onStop() {
        mSpeechRecognizer.stopListening()
    }

    override fun onResults(bundle: Bundle?) {
        bundle?.let {
            val result = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.d(ConstantsUtil.TAG, "onResults: ${result?.get(0)}")
            if (result != null) {
                view.text_view_confirm_body.text = result[0]
            }
        }
    }

    override fun onPartialResults(bundle: Bundle?) {
        Log.d(ConstantsUtil.TAG, "onPartialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(ConstantsUtil.TAG, "onEvent")
    }
}