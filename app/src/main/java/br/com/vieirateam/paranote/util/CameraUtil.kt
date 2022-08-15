package br.com.vieirateam.paranote.util

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.fragment.BaseNoteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.*
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraUtil(
    private val baseNoteFragment: BaseNoteFragment,
    private val view: View,
    private val bottomSheet: BottomSheet.Builder
): ImageAnalysis.Analyzer {

    private lateinit var mExecutorService: ExecutorService
    private lateinit var mProcessCameraProvider: ProcessCameraProvider
    private lateinit var mCameraInfo: CameraInfo
    private lateinit var mCameraControl: CameraControl
    private lateinit var mImageAnalysis: ImageAnalysis

    private var resultText: String? = null
    private val previewView = view.camera_view
    private val floatingButton = view.floating_button_camera
    private lateinit var mBuilder: BottomSheet.Builder
    private val recognizer = TextRecognition.getClient()
    private val context = baseNoteFragment.activity as AppCompatActivity
    private val mCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    fun startCamera() {
        mExecutorService = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            mProcessCameraProvider = cameraProviderFuture.get()
            configureAnalyzer()
            configureCamera()
        }, ContextCompat.getMainExecutor(context))

        floatingButton.setOnClickListener {
            val result = getResultText().toString().trim()
            if (result.isNotEmpty()) {
                shutdownCamera()
                showBottomText()
            }
        }
    }

    private fun configureAnalyzer() {
        mImageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        mImageAnalysis.setAnalyzer(mExecutorService, this)
    }

    private fun configureCamera() {
        val preview = Preview.Builder().build()
        try {
            mProcessCameraProvider.unbindAll()
            mProcessCameraProvider.bindToLifecycle(context, mCameraSelector, preview, mImageAnalysis).apply {
                mCameraInfo = cameraInfo
                mCameraControl = cameraControl
                configureZoom()
                configureFlash()
            }
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exception: Exception) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(inputImage).addOnSuccessListener { result ->
                if(result.text.trim().isNotEmpty()) {
                    resultText = result.text
                }
            }
            .addOnFailureListener { exception ->
                resultText = null
                Log.d(ConstantsUtil.TAG, exception.toString())
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
        } ?: imageProxy.close()
    }

    private fun showBottomText() {
        mBuilder = BottomSheet.Builder(context, R.layout.bottom_sheet_confirm, callbacks)
        mBuilder.build()
        val view = mBuilder.getBottomSheetView()
        view.image_view_confirm.setImageResource(R.drawable.ic_drawable_copy)
        view.text_view_confirm_title.text = context.getString(R.string.text_view_scan_text)
        view.text_view_confirm_body.text = getResultText()

        view.image_view_confirm.setOnClickListener {
            OptionsUtil.copy(context, resultText.toString())
        }

        view.button_cancel.setOnClickListener {
            mBuilder.setHide()
            resultText = null
            startCamera()
        }

        view.button_confirm.setOnClickListener {
            val text = view.text_view_confirm_body.text.toString()
            shutdownCamera()
            mBuilder.setHide()
            bottomSheet.setHide()
            configureBottomSheetText(text)
        }
    }

    private fun configureBottomSheetText(result: String) {
        if (result.trim().isNotEmpty()) {
            baseNoteFragment.note = Note()
            baseNoteFragment.note.body = result
            baseNoteFragment.configureBottomSheetText(baseNoteFragment.note, true)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureZoom() {
        val scaleGestureDetector = ScaleGestureDetector(context, zoomListener)
        previewView.setOnTouchListener { _, event ->
            bottomSheet.setLock(false)
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP){
                bottomSheet.setLock(true)
            }
            return@setOnTouchListener true
        }
    }

    private fun configureFlash() {
        mCameraInfo.torchState.observe(context) { state ->
            if (state == TorchState.OFF) {
                view.image_view_send.setImageResource(R.drawable.ic_drawable_flash_off)
            } else {
                view.image_view_send.setImageResource(R.drawable.ic_drawable_flash_on)
            }
        }
    }

    private var zoomListener = object: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val currentZoomRatio = mCameraInfo.zoomState.value?.zoomRatio ?: 0F
            val delta = detector.scaleFactor
            mCameraControl.setZoomRatio(currentZoomRatio + delta)
            return super.onScale(detector)
        }
    }

    fun setFlash() {
        if(mCameraInfo.hasFlashUnit()) {
            mCameraControl.enableTorch(mCameraInfo.torchState.value == TorchState.OFF)
        }
    }

    @SuppressLint("RestrictedApi")
    fun shutdownCamera() {
        mImageAnalysis.clearAnalyzer()
        mProcessCameraProvider.unbindAll()
    }

    fun shutdownFlash() {
        if (mCameraInfo.hasFlashUnit()) {
            mCameraControl.enableTorch(false)
        }
    }

    private fun getResultText(): String? = resultText

    private val callbacks = object: BottomSheet.Callback {
        override fun onBackPressed() {
            if(::mBuilder.isInitialized) {
                mBuilder.setHide()
            }
            startCamera()
        }

        override fun onClickListener(view: View) { TODO("Not yet implemented") }

        override fun onStartTextRecognition(dialog: BottomSheetDialog) { TODO("Not yet implemented") }

        override fun onFinishTextRecognition() { TODO("Not yet implemented") }
    }
}